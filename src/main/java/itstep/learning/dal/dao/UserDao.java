package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class UserDao {
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
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
            return true;
        }
        catch (SQLException ex){
            System.err.println("UserDao::installUsersAccess " +
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
            return true;
        }
        catch (SQLException ex){
            System.err.println("UserDao::installUsers " +
                    ex.getMessage());
        }
        return false;
    }
}
