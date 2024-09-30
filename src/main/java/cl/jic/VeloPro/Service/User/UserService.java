package cl.jic.VeloPro.Service.User;

import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Repository.UserRepo;
import cl.jic.VeloPro.Security.TokenSecurity;
import cl.jic.VeloPro.Utility.EmailService;
import cl.jic.VeloPro.Validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired private UserRepo userRepo;
    @Autowired private UserValidator validator;
    @Autowired private EmailService emailService;
    @Autowired private TokenSecurity token;

    @Override
    public void addUser(User user) {
        User userDB = getUserCreated(user.getRut());
        System.out.println("USER DB- "+userDB);
        if (userDB != null){
            throw new IllegalArgumentException("Usuario Existente: Ya hay existe el usuario");
        } else {
            validator.validate(user);
            user.setStatus(true);
            user.setDate(LocalDate.now());
            user.setName(user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1));
            user.setSurname(user.getSurname().substring(0, 1).toUpperCase() + user.getSurname().substring(1));
            System.out.println("USER INGRESADO: "+user);
            User user2 = getUser(user.getUsername());
            if (user2 != null && user2.getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new IllegalArgumentException("Este nombre de usuario ya existe");
            }
            userRepo.save(user);
            System.out.println("GUARDO DATOS");
        }
    }

    @Override
    public void updateUser(User user) {
        validator.validate(user);
        user.setName(user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1));
        user.setSurname(user.getSurname().substring(0, 1).toUpperCase() + user.getSurname().substring(1));
        user.setRut(user.getRut());
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        user.setToken(user.getPassword());
        userRepo.save(user);
    }

    @Override
    public void deleteUser(User user) {
        user.setStatus(false);
        userRepo.save(user);
    }

    @Override
    public void activateUser(User user) {
        user.setStatus(true);
        user.setDate(LocalDate.now());
        userRepo.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    public User getAuthUser(String username, String pass) {
        Optional<User> optionalUser = userRepo.findByUsernameAndPassword(username, pass);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            validator.validateStatus(user.isStatus());
            return user;
        }
        return null;
    }

    @Override
    public User getAuthUserToken(String username, String token) {
        Optional<User> optionalUser = userRepo.findByUsernameAndToken(username, token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            validator.validateStatus(user.isStatus());
            return user;
        }
        return null;
    }

    @Override
    public User getUser(String username) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        return optionalUser.orElse(null);
    }

    @Override
    public void sendEmailCode(User user) {
        String code = token.recoverPassword();
        emailService.sendCodePass(user, code);
        user.setToken(code);
        userRepo.save(user);
    }

    @Override
    public void updatePassword(User user, String password) {
        validator.validateChangePassword(user, password);
        user.setPassword(password);
    }

    private User getUserCreated(String rut){
        Optional<User> user = userRepo.findByRut(rut);
        return user.orElse(null);
    }
}
