package itstep.learning.dal.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRoleDao {
    private final Connection connection;

    public UserRoleDao(Connection connection) {
        this.connection = connection;
    }

    public boolean installUserRolesTable(){
        String sql = "CREATE TABLE IF NOT EXISTS userRoles ("
                + "userRole_id VARCHAR(20) PRIMARY KEY,"
                + "description VARCHAR(255) NOT NULL,"
                + "can_create TINYINT(1) NOT NULL,"
                + "can_read TINYINT(1) NOT NULL DEFAULT 1,"
                + "can_update TINYINT(1) NOT NULL,"
                + "can_delete TINYINT(1) NOT NULL"
                + ") Engine = InnoDB DEFAULT CHARSET = utf8mb4";
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            System.err.println("UserRoleDao::installUserRoles " +
                    ex.getMessage());
        }
        return false;
    }
}
