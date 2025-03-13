package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import itstep.learning.services.db.DbService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@Singleton
public class DataContext {
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final AccessTokenDao accessTokenDao;

    @Inject
    public DataContext(Injector injector) throws SQLException {

        // аналог - userDao = new UserDao(connection, logger);
        userDao = injector.getInstance(UserDao.class);
        // інжектор вміє будувати об'єкти з класів, він може взяти клас,
        // проаналізувати що цьому класу треба і інжектувати це

        // аналог - userRoleDao = new UserRoleDao(connection);
        userRoleDao = injector.getInstance(UserRoleDao.class);
        accessTokenDao = injector.getInstance(AccessTokenDao.class);
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public UserRoleDao getUserRoleDao() {
        return userRoleDao;
    }

    public AccessTokenDao getAccessTokenDao() {
        return accessTokenDao;
    }
}
