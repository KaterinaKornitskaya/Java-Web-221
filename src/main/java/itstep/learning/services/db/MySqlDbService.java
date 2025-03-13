package itstep.learning.services.db;

import com.google.inject.Singleton;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class MySqlDbService implements DbService {
    private Connection connection;

    @Override
    public Connection getConnection() throws SQLException {
        if(connection == null) {
              // підключення до БД:
//            // реєструємо новий драйвер:
//            // при створенні нового обєкту DriverManager треба написати не просто new Driver,
//            // а саме new com.mysql.cj.jdbc.Driver()
//            DriverManager.registerDriver(
//                    new com.mysql.cj.jdbc.Driver()
//            );
//            // строка підключення, в кінці - назва БД
//            String connectionString = "jdbc:mysql://localhost:3306/java221";
//            // підключаємся до БД
//            Connection connection = DriverManager.getConnection(
//                    connectionString,
//                    "user221",
//                    "pass221"
//            );

            // ще один спосіб підключення до БД
            String connectionString = "jdbc:mysql://localhost:3306/java221"
                    + "?useUnicode=true&characterEncoding=UTF-8";
            MysqlDataSource mds = new MysqlDataSource();
            mds.setURL(connectionString);
            connection = mds.getConnection("user221", "pass221");
            connection.setAutoCommit(false);
        }
        return connection;
    }
}
