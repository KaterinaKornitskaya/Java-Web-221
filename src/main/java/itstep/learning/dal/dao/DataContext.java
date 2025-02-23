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
    private final Logger logger;
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final Injector injector;

    @Inject
    public DataContext(DbService dbService, Logger logger, Injector injector) throws SQLException {
        this.logger = logger;
        this.injector = injector;

        // аналог - userDao = new UserDao(connection, logger);
        userDao = injector.getInstance(UserDao.class);
        // інжектор вміє будувати об'єкти з класів, він може взяти клас,
        // проаналізувати що цьому класу треба і інжектувати це

        // аналог - userRoleDao = new UserRoleDao(connection);
        userRoleDao = injector.getInstance(UserRoleDao.class);
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public UserRoleDao getUserRoleDao() {
        return userRoleDao;
    }
}
