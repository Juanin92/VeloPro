package cl.jic.VeloPro.Session;

import cl.jic.VeloPro.Model.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class Session {
     private User currentUser;
}
