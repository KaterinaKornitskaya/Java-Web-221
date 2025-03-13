package itstep.learning.dal.dto;

import java.util.Date;
import java.util.UUID;
// Art. 17 GDPR – Right to erasure (‘right to be forgotten’) - General Data Protection Regulation (GDPR) (gdpr-info.eu)
// право бути забутим - всі користувачі мають мати право
// видалити всі персональні дані повністю без затримок

public class UserAccess {
    private UUID userAccessId;

    // зовнішній ключ до User
    private UUID userId;

    private String login;
    private String salt;
    private String dk;
    private String roleId;
    private java.util.Date deleteMoment;



    public UUID getUserAccessId() {
        return userAccessId;
    }

    public void setUserAccessId(UUID userAccessId) {
        this.userAccessId = userAccessId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Date getDeleteMoment() {
        return deleteMoment;
    }

    public void setDeleteMoment(Date deleteMoment) {
        this.deleteMoment = deleteMoment;
    }
}
