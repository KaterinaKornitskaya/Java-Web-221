package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.db.DbService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
public class UserRoleDao {
    private final Connection connection;

    @Inject
    public UserRoleDao(DbService dbService) throws SQLException {
        this.connection = dbService.getConnection();
    }

    public boolean installUserRolesTable(){
        String sql = "CREATE TABLE IF NOT EXISTS user_roles ("
                + "userRole_id VARCHAR(20) PRIMARY KEY,"
                + "description VARCHAR(255) NOT NULL,"
                + "can_create TINYINT(1) NOT NULL,"
                + "can_read TINYINT(1) NOT NULL,"
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
