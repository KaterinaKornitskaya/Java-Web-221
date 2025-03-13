package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class AccessTokenDao {

    private final Logger logger;
    private final DbService dbService;

    @Inject
    public AccessTokenDao (DbService dbService, Logger logger) throws SQLException {
        this.dbService = dbService;
        this.logger = logger;
    }

    // видача токену - create
    public AccessToken create(User user){
        return null;
    }

    // валідація токену - read
    public UserAccess getUserAccess(AccessToken token){
        return null;
    }

    // пролонгування токену - update
    public boolean prolongToken(AccessToken token){
        return true;
    }

    // скасування токену - delete
    public boolean cancelToken(AccessToken token){
        return true;
    }

    public boolean installTables(){
        String sql = "CREATE TABLE IF NOT EXISTS access_tokens("
                + "access_token_id  CHAR(36) PRIMARY KEY DEFAULT( UUID() ),"
                + "user_access_id   CHAR(36) NOT NULL,"
                + "issued_at        DATETIME NOT NULL,"
                + "expires_at       DATETIME NULL"
                + ") Engine = InnoDB, DEFAULT CHARSET = utf8mb4";
        try(Statement statement = dbService.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            dbService.getConnection().commit();
            logger.info("AccessTokenDao::installTables OK");
            return true;
        }
        catch (SQLException ex){
            logger.log(
                    Level.WARNING,
                    "AccessTokenDao::installTables {0} sql: '{1}'",
                    new Object[] { ex.getMessage(), sql}
            );
        }
        return false;
    }
}
