package itstep.learning.models;

import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;

// ця модель буде віддаватися на результат автентифікації
public class UserAuthViewModel {
    private User user;
    private UserAccess userAccess;
    private AccessToken accessToken;



    public UserAuthViewModel() {
    }

    public UserAuthViewModel(User user, UserAccess userAccess, AccessToken accessToken) {
        this.user = user;
        this.userAccess = userAccess;
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccess getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
