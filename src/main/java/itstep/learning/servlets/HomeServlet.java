package itstep.learning.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mysql.cj.jdbc.MysqlDataSource;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import itstep.learning.services.config.ConfigService;
import itstep.learning.services.datetime.DatetimeService;
import itstep.learning.services.db.DbService;
import itstep.learning.services.hash.HashService;
import itstep.learning.services.hash.Md5HashService;
import itstep.learning.services.kdf.KdfService;
import itstep.learning.services.random.RandomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.io.IOException;
import java.sql.*;
import itstep.learning.dal.dto.User;

// @ - анотації, аналог атрибутів [] в c#
// Servlet - це типу контроллер в c#
// указывает, что сервлет будет обрабатывать запросы по адресу /Home.
//@WebServlet("/Home") // WebServlet який буде відгукуватись на адресу /Home
@Singleton
public class HomeServlet extends HttpServlet {
    // final - типу readonly
    private final RandomService randomService;
    private final DatetimeService datetimeService;
    private final KdfService kdfService;
    private final DbService dbService;
    private final DataContext dataContext;
    private final RestService restService;
    private final ConfigService configService;

    // інжектуємо RandomService
    @Inject
    public HomeServlet(RandomService randomService, DatetimeService datetimeService, KdfService kdfService, DbService dbService, DataContext dataContext, RestService restService, ConfigService configService) {
        this.randomService = randomService;
        this.datetimeService = datetimeService;
        this.kdfService = kdfService;
        this.dbService = dbService;
        this.dataContext = dataContext;
        this.restService = restService;
        this.configService = configService;
    }

    // doGet — метод для обработки GET-запросов
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        String message = "";
        String message2;

        try {
            String sql = "SELECT CURRENT_TIMESTAMP";
            String sql2 = "SHOW DATABASES";
            // statement - це інструмент передачі запиту в БД
            // - аналог sql command
            Statement statement = dbService.getConnection().createStatement();

            // executeQuery(sql) - виконує наш sql запит
            // залежно від того, що ми хочемо, щоб нам повернулося, я такі варіанти:
            // - statement.execute - повертає boolean (успішно/неуспішно)
            // - statement.executeQuery - повертає ResultSet
            // ResultSet - це інструмент вичитування даних
            // - statement.executeUpdate - повертає к-сть рядків, що було задіяно запитом
            // - statement.executeBatch - пакетне виконання (декількох команд)
            // - та інші їх варіації
            ResultSet resultSet = statement.executeQuery(sql);
            // ResultSet - типу DataReader - це відображення табличних даних на мову програмування
            // ResultSet бере по 1-му рядку з таблиці (це та таблиця, в вигляді якої повертається
            // дані при запитах в субд (тіпа в менеджмент студіо і ін)

            // забрати дані з ResultSet:
            resultSet.next();  // тут дістаємо результат виконання sql запиту

            //  getString(1) - повертає один стовпчик
            message = resultSet.getString(1); // !! JDBC - індекс з 1
            //message = connection == null ? "NULL" : "OK";

            // щоб зробити декылька запитів і вевести, наприклад,
            // хочемо вівести і SELECT CURRENT_TIMESTAMP, і SHOW DATABASES разом

            // закриваємо resultSet перед тим, як записати в нього результат наступного запиту
            resultSet.close();
            // записуємо в нього ж результат наступного запиту
            resultSet = statement.executeQuery("SHOW DATABASES");
            StringBuilder sb = new StringBuilder();
            while(resultSet.next()) {
                sb.append(", ");
                sb.append(resultSet.getString(1));
            }
            resultSet.close();
            statement.close();
            message += sb.toString();


            // завдання - Відобразити результати запиту "SHOW DATABASES", передавши їх рядком через кому
            Statement statement2 = dbService.getConnection().createStatement();
            ResultSet resultSet2 = statement2.executeQuery(sql2);
            StringBuilder myDatabases = new StringBuilder();
            while (resultSet2.next()) { // тут перебираємо всі строки результату
                if(myDatabases.length() > 0){  // коли в myDatabases щось додасться - додаємо знак ", "
                    myDatabases.append(", ");
                }
                // дописуємо в myDatabases кожну наступну строку
                myDatabases.append(resultSet2.getString(1));
            }

            resultSet2.close();
            statement2.close();
            message2 = myDatabases.toString();
            // завдання закінчено

        } catch (SQLException ex) {
            //message = ex.getMessage();
            message2 = ex.getMessage();
        }

        String msg = dataContext.getUserDao().installTables()
                ? "Install Ok (UserTables)"
                : "Install Fail (UserTables)";

        String msg2 = dataContext.getUserRoleDao().installUserRolesTable()
                ? "Install Ok (UserRoleTable)"
                : "Install Fail (UserRoleTable)";

        String msg3 = dataContext.getAccessTokenDao().installTables()
                ? "Install Ok (AccessTokenTable)"
                : "Install Fail (AccessTokenTable)";

        String connectionString = "jdbc:mysql://localhost:3306/java221"
                + "?useUnicode=true&characterEncoding=UTF-8";
        String connectionString2 = "jdbc:"
                + configService.getValue("db.MySql.dbms").getAsString() + "://"
                + configService.getValue("db.MySql.host").getAsString() + ":"
                + configService.getValue("db.MySql.port").getAsInt() + "/"
                + configService.getValue("db.MySql.schema").getAsString() + "?"
                + configService.getValue("db.MySql.params").getAsString().replace("\\u003d", "=").replace("\\u0026", "&")
                ;

        restService.sendResponse(resp,
                new RestResponse()
                        .setStatus(200)
                        .setMessage(
                                "| myDatabases.toString(): " + message + " | "
                                + "| kdfService.dk: " + kdfService.dk("123", "456") + " | "
                                + "| randomService: randomInt(): " + randomService.randomInt() + " | "
                                + "| randomService: randomString(): " + randomService.randomString(8) + " | "
                                + "| randomService: randomFileName(): " + randomService.randomFileName(8) + " | "
                                + "| datetimeService.getCurrentDateTime(): " + datetimeService.getCurrentDateTime() + " | "
                                + "| datetimeService.getCurrentTimestamp(): " + datetimeService.getCurrentTimestamp() + " | "
                                + "| getUserDao().installTables(): " + msg + " | "
                                + "| getUserRoleDao().installUserRolesTable(): " + msg2 + " | "
                                + "| getAccessTokenDao().installTables(): " + msg3 + " | "
                                        + "| configService.getValue(): " + configService.getValue("db.MySql.port").getAsInt() + " | "
                                        + "| schema: " + configService.getValue("db.MySql.schema").getAsString() + " | "
                                        + "| params: " + configService.getValue("db.MySql.params").getAsString().replace("\\u003d", "=").replace("\\u0026", "&") + " | "
                                        + "| user: " + configService.getValue("db.MySql.user").getAsString() + " | "
                                        + "| password: " + configService.getValue("db.MySql.password").getAsString() + " | "
                                        + "| autocommit: " + configService.getValue("db.MySql.autocommit").getAsBoolean()
                                + "CONN STRING2: " + connectionString2 + " | "
                        )
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // приймаємо body
        // приймаємо байт-масив і формуємо з нього строку
        // String body = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        // тепер отримані дані стрінг треба перетворити в джсон


        // нам потрібні будуть моделі форм, які ми будемо приймати і обробляти
        // для цього створюємо package itstep.learning.model, в який
        // додаємо новий java class UserSignupFormModel
        UserSignupFormModel model;

        // частково формуємо відповідь клієнту
        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("POST /home")
                        .setCacheTime(0)
                        .setMeta(Map.of(
                                "dataType", "object",
                                "read", "GET /home",
                                "update", "PUT /home",
                                "delete", "DELETE /home"
                        ));

        try{
            // парсимо модель в gson
            model = restService.fromBody(req, UserSignupFormModel.class);
            // .class - типу typeof, .class повертає цей обєкт
        }
        catch(IOException ex){
            // якщо не змогли розпарсити модель - повертаємо 422
            restService.sendResponse(resp, restResponse
                    .setStatus(422)
                    .setMessage(ex.getMessage())
            );
            return;
        }

        // серверна валідація:
        // реєстрація користувача

        User user = dataContext.getUserDao().addUser(model);

        // доформуємо відповіді (почали формувати вище RestResponse restResponse = ...)
        // якщо не зміг обробити - повертаємо несформовану модель
        if( user == null ){
            // 507 - значить помилка роботи з даними
            restResponse
                    .setStatus(507)
                    .setMessage("DB error")
                    .setData(model)  // model вище розібрали з джсон
            ;
        }
        // якщо зміг обробити - повертаємо заповненого юзера
        else{
            // 201 значить created
            restResponse
                    .setStatus(201)
                    .setMessage("User created.")
                    .setData(user)  // model вище розібрали з джсон
            ;
        }
        // надсилаємо сформовану відповідь клієнту в форматі джсон
        restService.sendResponse(resp, restResponse);
    }


    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        restService.setCorsHeaders(resp);
    }
}

/*
* Асинхронне виконання коду
* Синхронне - послідовне (у часі)
* Задача1 Задача2 Задача3 - виконуються один за одним
*
* Асинхронність - будь-яке відхилення від синхронності
* Чиста паралельність - паралельне одночасне виконання:
* Задача1
* Задача2
* Задача3
*
* Ниткового типу - якісь задачі послідовні, якісь парелельно
* Задача1 Задача2
* Задача3
*
* Перемикання - паралельне з перемиканням
* Частина задачі1   Наступна частина задачі 1    Наступна частина задачі 1
*   Частина задачі 2    Наступна частина задачі 2   Наступна частина задачі 2
*       Частина задачі 3    Наступна частина задачі 3   Наступна частина задачі 3
*
*
* Способи раеалізації асинхронності:
* 1) Багатозадачність - засоби мови програмування (Task, Future, Promise)
* 2) Багатопоточність - засоби операційної системи(за наявності)
* 3) Багатопроцесність - засоби операційної системи
* (потоки працюють всередині процесів)
* 4) Мережні (grid, network) технології
*
*
* Поганий варіант писати так:
* async fun() {...}
* res = await fun();
*
* Краще так:
* task = fun();
* ставимо таска ы запускаэмо ф-ію, яка повертає таск або проміс або фьючер
* other work
* res = await task; тільки коли потрібно - чекаємо ф-ію
*
* Або:
* res = await fun().then(...).then(...)
* тут ми вибудовуємо задачі в нитку, а результат всієї нитки
* вже треба чекати
* В c# замість then використовуємо continueWith
*
* Або:
fun().then(...).then(...).then(res => ...)
* це - нитка (Continuations)
* fun().then(.2.).then(.2.).then(res2 => ...) -
* це друга нитка, яка стартує паралельно з першою
*
*
* */
