package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


@Singleton
public class UserDao {
    private final Connection connection;
    private final Logger logger;
    private final KdfService kdfService;
    private final DbService dbService;

    @Inject
    public UserDao (DbService dbService, Logger logger, KdfService kdfService) throws SQLException {
        this.dbService = dbService;
        this.connection = dbService.getConnection();
        this.logger = logger;
        this.kdfService = kdfService;
    }

    public User addUser(UserSignupFormModel userModel){
        User user = new User();
        // генеруємо UUID
        user.setUserId(UUID.randomUUID());

        user.setName(userModel.getName());
        user.setEmail(userModel.getEmail());
        user.setPhone(userModel.getPhoneNumbers().get(0));
        user.setBirthday(userModel.getBirthDate());
        //user.setLogin(userModel.getLogin());
        user.setAddress(userModel.getAddress());

        // реєстрація юзера
        // використовуємо параметризовані запити (а не вставляємо
        // чистий стрінг в sql)
        String sql = "INSERT INTO users (user_id, name, email, phone, address, birthdate)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement prep = this.dbService.getConnection().prepareStatement(sql)){
            // перший параметр - номер VALUES, і в jdbc вони починаються з 1, а не з 0
            prep.setString(1, user.getUserId().toString() );
            prep.setString(2, user.getName() );
            prep.setString(3, user.getEmail() );
            prep.setString(4, user.getPhone() );
            prep.setString(5, user.getAddress() );
            prep.setString(6, user.getBirthday().toString() );
            //prep.setString(7, user.getLogin() );

            //this.dbService.getConnection().setAutoCommit(false);
            //this.connection.setAutoCommit(false);
            prep.executeUpdate();
        }
        catch (SQLException ex){
            logger.warning("UserDao::addUser " + ex.getMessage());

            // відкат транзакції
            try { this.dbService.getConnection().rollback(); }
            catch (SQLException exIgnore) { }

            return null;
        }

        // на місці role_id зразу вказали guest
        // - тому що самореєстрація - це тільки guest
        sql = "INSERT INTO users_access (user_access_id, user_id, role_id, login, salt, dk)"
                + " VALUES ( UUID(), ?, 'guest', ?, ?, ?)";

        //try(PreparedStatement prep = this.connection.prepareStatement(sql))
        try(PreparedStatement prep = this.dbService.getConnection().prepareStatement(sql)){
            // перший параметр - номер VALUES, і в jdbc вони починаються з 1, а не з 0
            prep.setString(1, user.getUserId().toString() );
            prep.setString(2, user.getEmail() );
            String salt = UUID.randomUUID().toString().substring(0, 16);
            prep.setString(3, salt );
            prep.setString(4, kdfService.dk(userModel.getPassword(), salt) );
            prep.executeUpdate();

            // фіксуємо транзакцію
            this.dbService.getConnection().commit();
            //this.connection.commit();
        }
        catch (SQLException ex){
            logger.warning("UserDao::addUser " + ex.getMessage());

            // відкат транзакції
            try { this.dbService.getConnection().rollback(); }
            catch (SQLException exIgnore) { }

            return null;
        }


        return user;
    }

    public User getUserById(String id){
        UUID uuid;
        try{
            uuid = UUID.fromString(id);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "UserDao:: getUserById Parse error: {0}", id);
            return null;
        }
        return  getUserById(uuid);
    }

    public User getUserById(UUID uuid){
        String sql = String.format(
                "SELECT u.* FROM users u WHERE u.user_id = '%s'",
                uuid.toString()
        );
        try(Statement stmt = dbService.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return User.fromResultSet(rs);
            }
        }
        catch (SQLException ex){
            logger.log(
                    Level.WARNING,
                    "UserDao:: getUserById {0}, {1}",
                    new Object[] {ex.getMessage(), sql} );
        }
        return null;
    }

    public boolean update(User user){
        // два підходи для update
        // 1) змінювати все що не null
        // 2) змінювати все

        // заготовлюємо параметри, які будемо апдейтити
        Map<String, Object> data = new HashMap<>();
        if(user.getName() != null){
            data.put("name", user.getName());
        }
        if(user.getEmail() != null){
            data.put("email", user.getEmail());
        }
        if(user.getPhone() != null){
            data.put("phone", user.getPhone());
        }
        if(user.getAddress() != null){
            data.put("address", user.getAddress());
        }
        if(user.getBirthday() != null){
            data.put("birthdate", user.getBirthday());
        }
        if(data.isEmpty()) return true;

        // TODO: convert to StringBuilder
        StringBuilder sqlUsers = new StringBuilder("UPDATE users SET ");
        boolean isFirst = true;

        // збираємо sql
        for(Map.Entry<String, Object> key : data.entrySet()){
            if(!isFirst) sqlUsers.append(", ");
            sqlUsers.append(key.getKey()).append(" = ?");
            isFirst = false;
        }
        sqlUsers.append(" WHERE user_id = ?");
        // циклічно зібрали update

        // Если email изменяется, добавляем второй запрос для users_access
        String sqlUsersAccess = null;
        boolean updateAccess = user.getEmail() != null;
        if (updateAccess) {
            sqlUsersAccess = "UPDATE users_access SET login = ? WHERE user_id = ?";
        }

        try {

            // 1. Обновляем users
            try (PreparedStatement prepUsers = dbService.getConnection().prepareStatement(sqlUsers.toString())) {
                int param = 1;
                for (Object value : data.values()) {
                    prepUsers.setObject(param++, value);
                }
                prepUsers.setString(param, user.getUserId().toString());
                prepUsers.executeUpdate();
                //dbService.getConnection().commit();
            }

            // 2. Обновляем users_access, если изменяется email
            if (updateAccess) {
                try (PreparedStatement prepAccess = dbService.getConnection().prepareStatement(sqlUsersAccess)) {
                    prepAccess.setString(1, user.getEmail());
                    prepAccess.setString(2, user.getUserId().toString());
                    prepAccess.executeUpdate();
                    //dbService.getConnection().commit();
                }
            }
            dbService.getConnection().commit();
            return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "UserDao::update {0}", ex.getMessage());
            return false;
        }
    }

    public UserAccess authorize(String login, String password){
        // SELECT * FROM users_access ua
        // JOIN users u ON ua.user_id = u.user_id
        // WHERE ua.login = 'ketrinradchenko@gmail.com'
        String sql =
                "SELECT * FROM users_access ua " +
                //"JOIN users u ON ua.user_id = u.user_id " +
                "WHERE ua.login = ?";
        // створюємо підготовлений запит
        try(PreparedStatement prep = dbService.getConnection().prepareStatement(sql)){
            prep.setString(1, login);
            ResultSet rs = prep.executeQuery();
            // перевіряємо що є хоч якісь дані
            if(rs.next()){
                // знайшлт за логіном, через логін шукаємо сіль,
                // через сіль прораховуємо dk
                String dk = kdfService.dk(password, rs.getString("salt"));
                // тепер порівнюємо - dk який розрахували вище через
                // введений пароль, та dk який збережено в базі
                // рівність стрінгів в джава перевіряємо через Objects.equals()
                if(Objects.equals(dk, rs.getString("dk"))){
                    // якщо збігаються dk - будуємо і повертаємо user

                    // дві класичні форми фабричних методів
                    // 1) через конструктор - new User(rs)
                    // 2) через статік методи - User.fromResultSet(rs)
                    return UserAccess.fromResultSet(rs);
                }
            }
        }
        catch (SQLException ex){
            logger.log(Level.WARNING, "UserDao::authorize {0}", ex.getMessage());
        }
        return null;
    }

    public CompletableFuture deleteAsync(User user){
        // видаляємо тільки особисту інфо, а id лишаємо
        // тому наш delete - це по суті update
        String sql1 = String.format(
                "UPDATE users SET delete_moment = CURRENT_TIMESTAMP," +
                " name = '', email = '', phone = NULL, address = NULL, birthdate = NULL WHERE user_id = '%s'",
                user.getUserId().toString() );

        String sql2 = String.format(
                "UPDATE users_access SET ua_delete_dt = CURRENT_TIMESTAMP," +
                " login = UUID() WHERE user_id = '%s'",
                user.getUserId().toString() );

        // виконуємо два запити вище паралельно
        CompletableFuture task1 = CompletableFuture.runAsync(() -> {
            try(Statement stmt = dbService.getConnection().createStatement()){
                stmt.executeUpdate(sql1);
            }
            catch (SQLException ex){
                logger.log(Level.WARNING, "UserDao::delete1 {0}", ex.getMessage());

                // якщо запит не пройшов - скасовуємо транзакцію
                try {
                    dbService.getConnection().rollback();
                }
                catch (SQLException ignore){}
            }
        });

        CompletableFuture task2 = CompletableFuture.runAsync(() -> {
            try(Statement stmt = dbService.getConnection().createStatement()){
                stmt.executeUpdate(sql2);
            }
            catch (SQLException ex){
                logger.log(Level.WARNING, "UserDao::delete2 {0}", ex.getMessage());

                // якщо запит не пройшов - скасовуємо транзакцію
                try {
                    dbService.getConnection().rollback();
                }
                catch (SQLException ignore){}
            }
        });
        return CompletableFuture
                .allOf(task1, task2)
                .thenRun( () -> {
                    try {
                        dbService.getConnection().commit();
                    }
                    catch (SQLException ignore){}
                });

//        try{
//            task1.get();  // analog - await task1
//            task2.get();
//        }
//        catch (ExecutionException | InterruptedException ignore) {}


    }

    public boolean installTables(){
        // запускаємо таски
        Future<Boolean> task1 = CompletableFuture
                .supplyAsync(this::installUsersAccess);
        Future<Boolean> task2 = CompletableFuture
                .supplyAsync(this::installUsers);

        try{
            boolean res1 = task1.get();  // analog - await task1
            boolean res2 = task2.get();

            // всі команди, які щось змінюють в БД -
            // мають комітитись після виконання
            try{ dbService.getConnection().commit(); }
            catch (SQLException ignore) {}

            return res1 && res2;
        }
        catch (ExecutionException | InterruptedException ignore){
            return false;
        }
        // тепер при виклику методу installTables()
        // він запускає дві задача асинхронно,
        // поки одна задача чекається - друга виконується


        // в звичайному варіанті в цьому методі є тільки
        // ретурн нижче
        // все що вище - робимо асинхронний варіант
        //return installUsers() && installUsersAccess();
    }

    private boolean installUsersAccess(){
        String sql = "CREATE TABLE IF NOT EXISTS users_access("
                + "user_access_id CHAR(36) PRIMARY KEY DEFAULT( UUID() ),"
                + "user_id  CHAR(36) NOT NULL,"
                + "role_id  VARCHAR(16) NOT NULL,"
                + "login    VARCHAR(128) NOT NULL,"
                + "salt     CHAR(16) NOT NULL,"
                + "dk       CHAR(20) NOT NULL,"
                + "ua_delete_dt DATETIME NULL,"
                + "UNIQUE(login)"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("UserDao::installUsers OK");
            return true;
        }
        catch (SQLException ex){
            logger.warning("UserDao::installUsersAccess " +
                    ex.getMessage());
        }
        return false;
    }

    private boolean installUsers(){
        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "user_id  CHAR(36) PRIMARY KEY DEFAULT( UUID() ),"
                + "name     VARCHAR(128) NOT NULL,"
                + "email    VARCHAR(256) NOT NULL,"
                + "phone    VARCHAR(32) NULL,"
                //+ "login    VARCHAR(56) NOT NULL,"
                + "address  VARCHAR(255) NULL,"
                + "birthdate DATE NULL,"
                + "delete_moment DATETIME NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("UserDao::installUsers OK");
            return true;
        }
        catch (SQLException ex){
            logger.warning("UserDao::installUsers " +
                    ex.getMessage());
        }
        return false;
    }
}
