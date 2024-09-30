package cl.jic.VeloPro.Repository;

import cl.jic.VeloPro.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByUsernameAndToken(String username, String token);
    Optional<User> findByUsername(String username);
    Optional<User> findByRut(String rut);
}
