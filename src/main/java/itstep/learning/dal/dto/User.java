package itstep.learning.dal.dto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class User {
    // UUID - like GUID in ASP
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private Timestamp birthday;
    private String address;
    private String login;

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(UUID.fromString(rs.getString("user_id")));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        Timestamp timestamp = rs.getTimestamp("birthdate");
        user.setBirthday(timestamp == null ? null : new Timestamp(timestamp.getTime()));
        user.setAddress(rs.getString("address"));
        user.setLogin(rs.getString("login"));
        return user;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
