package itstep.learning.servlets;

import com.google.gson.Gson;
import itstep.learning.rest.RestResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

// @ - анотації, аналог атрибутів [] в c#
// Servlet - це типу контроллер в c#
@WebServlet("/Home") // WebServlet який буде відгукуватись на адресу /Home
public class HomeServlet extends HttpServlet {
    // final - типу readonly
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        String message;
        String message2;
        // реєструємо новий драйвер:
        // при створенні нового обєкту DriverManager
        // треба написати не просто new Driver,
        // а саме new com.mysql.cj.jdbc.Driver()
        try {
            DriverManager.registerDriver(
                    new com.mysql.cj.jdbc.Driver()
            );
            // строка підключення, в кінці - назва БД
            String connectionString = "jdbc:mysql://localhost:3306/java221";
            // підключаємся до БД
            Connection connection = DriverManager.getConnection(
                    connectionString,
                    "user221",
                    "pass221"
            );
            String sql = "SELECT CURRENT_TIMESTAMP";

            String sql2 = "SHOW DATABASES";
            // statement - це інструмент передачі запиту в БД
            // - аналог sql command
            Statement statement = connection.createStatement();
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
            resultSet.next();
            // тут дістаємо результат виконання sql запиту

            // завдання - Відобразити результати запиту "SHOW DATABASES", передавши їх рядком через кому
            Statement statement2 = connection.createStatement();
            ResultSet resultSet2 = statement2.executeQuery(sql2);
            StringBuilder myDatabases = new StringBuilder();
            while (resultSet2.next()) { // тут перебираємо всі строки результату
                if(myDatabases.length() > 0){  // коли в myDatabases щось додасться - додаємо знак ", "
                    myDatabases.append(", ");
                }
                // дописуємо в myDatabases кожну наступну строку
                myDatabases.append(resultSet2.getString(1));
            }
            message2 = myDatabases.toString();
            // завдання закінчено

            //  getString(1) - повертає один стовпчик
            message = resultSet.getString(1); // !! JDBC - індекс з 1

            //message = connection == null ? "NULL" : "OK";
        } catch (SQLException ex) {
            //message = ex.getMessage();
            message2 = ex.getMessage();
        }


        resp.setContentType("application/json");
        resp.getWriter().print(
              gson.toJson(
                   new RestResponse()
                           .setStatus(200)
                           .setMessage(message2)
              )
        );

    }
}
