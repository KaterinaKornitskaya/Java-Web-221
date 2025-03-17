package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.services.config.ConfigService;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.*;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class AccessTokenDao {

    private final Logger logger;
    private final DbService dbService;
    private final ConfigService configService;

    private int tokenLifetime;


    @Inject
    public AccessTokenDao (DbService dbService, Logger logger, ConfigService configService) throws SQLException {
        this.dbService = dbService;
        this.logger = logger;
        this.configService = configService;
        this.tokenLifetime = 0;

    }

    // видача токену - create
    public AccessToken createToken(UserAccess userAccess){
        // токен створюємо тоді, коли юзер автентифікується

        if(userAccess == null) {
            return null;
        }

        if(tokenLifetime == 0) {
            tokenLifetime = 1000 *  // in milliseconds
                    configService.getValue("token.lifetime").getAsInt();
        }

        AccessToken token = new AccessToken();
        token.setAccessTokenId(UUID.randomUUID());
        token.setUserAccessId(userAccess.getUserAccessId());
        Date date = new Date();
        token.setIssuedAt(date);
        token.setExpiresAt(new Date(date.getTime() + 300*1000));

        String sql = "INSERT INTO access_tokens (access_token_id, user_access_id, issued_at, expires_at) " +
                "VALUES (?, ?, ?, ?)";
        try(PreparedStatement prep = dbService.getConnection().prepareStatement(sql)){
            prep.setString(1, token.getAccessTokenId().toString());
            prep.setString(2, token.getUserAccessId().toString());
            prep.setTimestamp(3, new Timestamp(token.getIssuedAt().getTime()));
            prep.setTimestamp(4, new Timestamp(token.getExpiresAt().getTime()));
            prep.executeUpdate();
            dbService.getConnection().commit();
        }
        catch (SQLException ex){
            logger.log(
                    Level.WARNING,
                    "AccessTokenDao::createToken {0} sql: '{1}'",
                    new Object[] { ex.getMessage(), sql}
            );
            return null;
        }

        return token;
    }

    // валідація токену - read
    public UserAccess getUserAccess(String bearerCredentials){
        UUID accessTokenId;
        try {
            accessTokenId = UUID.fromString(bearerCredentials);
        }
        catch (Exception ignore){ return null; }

        // шукаємо токен в таблиці
        String sql = String.format(
                "SELECT * FROM access_tokens a "
                + " JOIN users_access ua ON a.user_access_id = ua.user_access_id "
                + " WHERE a.access_token_id = '%s' "
                + " AND a.expires_at > CURRENT_TIMESTAMP",
                accessTokenId.toString() );

        try(Statement statement = dbService.getConnection().createStatement()){
            ResultSet rs = statement.executeQuery(sql);
            if( rs.next() ){
                return UserAccess.fromResultSet(rs);
            }
        }
        catch(SQLException ex) {
            logger.log(
                    Level.WARNING,
                    "AccessTokenDao:: getUserAccess {0} sql: '{1}'",
                    new Object[] { ex.getMessage(), sql}
            );
        }
        return null;
    }

    // перевірка, чи є у користувача активний токен
    public AccessToken getActiveToken(UUID userId) {

        // шукаємо токени цього користувача, термін дії яких
        // ще дійсний, сортуємо і беремо перший, самий свіжий
        String sql = "SELECT a.* FROM access_tokens a " +
                "JOIN users_access ua ON a.user_access_id = ua.user_access_id " +
                "WHERE ua.user_id = ? " +
                "AND a.expires_at > NOW() " +
                "ORDER BY a.expires_at DESC " +
                "LIMIT 1";


        // готуємо запит, підставляємо userId
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId.toString());
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {  // якщо знайшли діючий токен - повертаємо його
                AccessToken token = AccessToken.fromResultSet(rs);
                //logger.info("Found active token: " + token.getAccessTokenId());
                return token;
            }
            else {
                //logger.warning("No active token found for user: " + userId);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "AccessTokenDao::getActiveToken {0} sql: '{1}'",
                    new Object[]{ex.getMessage(), sql});
        }
        return null;
    }

    // пролонгування токену - update
    public AccessToken extendToken(AccessToken token) {

        // змінюємо expires_at
        String sql = "UPDATE access_tokens SET expires_at = ? WHERE access_token_id = ?";
        Date newExpiry = new Date(System.currentTimeMillis() + tokenLifetime);

        // готуємо запит
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setTimestamp(1, new Timestamp(newExpiry.getTime()));
            prep.setString(2, token.getAccessTokenId().toString());
            int affectedRows = prep.executeUpdate();
            dbService.getConnection().commit();

            // якщо щось було змінено в БД - то змінюємо обїкт токену
            if (affectedRows > 0) {
                token.setExpiresAt(newExpiry);
                //logger.info("Token extended: " + token.getAccessTokenId());
                return token;
            }
            else {
                //logger.warning("Failed to extend token: " + token.getAccessTokenId());
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "AccessTokenDao::extendToken {0} sql: '{1}'",
                    new Object[]{ex.getMessage(), sql});
        }
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
