package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.db.DbService;

import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class DataContext {
    private final Connection connection;
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;

    @Inject
    public DataContext(DbService dbService) throws SQLException {
        this.connection = dbService.getConnection();
        userDao = new UserDao(connection);
        userRoleDao = new UserRoleDao(connection);
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public UserRoleDao getUserRoleDao() {
        return userRoleDao;
    }
}
