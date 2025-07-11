package cl.jic.VeloPro.Service.User;

import cl.jic.VeloPro.Model.Entity.User;

import java.util.List;

public interface IUserService {

    void addUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    void activateUser(User user);
    List<User> getAllUser();
    User getAuthUser(String username, String pass);
    User getAuthUserToken(String username, String token);
    User getUser(String username);
    void sendEmailCode(User user);
    void sendEmailUpdatePassword(User user);
}
