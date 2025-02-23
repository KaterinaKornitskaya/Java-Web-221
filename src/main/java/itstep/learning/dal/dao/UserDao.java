package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Logger;


@Singleton
public class UserDao {
    private final Connection connection;
    private final Logger logger;
    private final KdfService kdfService;

    @Inject
    public UserDao (DbService dbService, Logger logger, KdfService kdfService) throws SQLException {
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

        // реєстрація юзера
        // використовуємо параметризовані запити (а не вставляємо
        // чистий стрінг в sql)
        String sql = "INSERT INTO users (user_id, name, email, phone)"
                + " VALUES (?, ?, ?, ?)";

        try(PreparedStatement prep = this.connection.prepareStatement(sql)){
            // перший параметр - номер VALUES, і в jdbc вони починаються з 1, а не з 0
            prep.setString(1, user.getUserId().toString() );
            prep.setString(2, user.getName() );
            prep.setString(3, user.getEmail() );
            prep.setString(4, user.getPhone() );
            this.connection.setAutoCommit(false);
            prep.executeUpdate();
        }
        catch (SQLException ex){
            logger.warning("UserDao::addUser " + ex.getMessage());

            // відкат транзакції
            try { this.connection.rollback(); }
            catch (SQLException exIgnore) { }

            return null;
        }

        // на місці role_id зразу вказали guest
        // - тому що самореєстрація - це тільки guest
        sql = "INSERT INTO users_access (user_access_id, user_id, role_id, login, salt, dk)"
                + " VALUES ( UUID(), ?, 'guest', ?, ?, ?)";

        try(PreparedStatement prep = this.connection.prepareStatement(sql)){
            // перший параметр - номер VALUES, і в jdbc вони починаються з 1, а не з 0
            prep.setString(1, user.getUserId().toString() );
            prep.setString(2, user.getEmail() );
            String salt = UUID.randomUUID().toString().substring(0, 16);
            prep.setString(3, salt );
            prep.setString(4, kdfService.dk(userModel.getPassword(), salt) );
            prep.executeUpdate();

            // фіксуємо транзакцію
            connection.commit();
        }
        catch (SQLException ex){
            logger.warning("UserDao::addUser " + ex.getMessage());

            // відкат транзакції
            try { this.connection.rollback(); }
            catch (SQLException exIgnore) { }

            return null;
        }


        return user;
    }

    public boolean installTables(){
        return installUsers() && installUsersAccess();
    }

    private boolean installUsersAccess(){
        String sql = "CREATE TABLE IF NOT EXISTS users_access("
                + "user_access_id CHAR(36) PRIMARY KEY DEFAULT( UUID() ),"
                + "user_id  CHAR(36) NOT NULL,"
                + "role_id  VARCHAR(16) NOT NULL,"
                + "login    VARCHAR(128) NOT NULL,"
                + "salt     CHAR(16) NOT NULL,"
                + "dk       CHAR(20) NOT NULL,"
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
                + "email    VARCHAR(256) NULL,"
                + "phone    VARCHAR(32) NULL"
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
