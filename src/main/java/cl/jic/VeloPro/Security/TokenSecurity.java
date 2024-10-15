package cl.jic.VeloPro.Security;

import cl.jic.VeloPro.Utility.NotificationManager;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TokenSecurity {
    public String recoverPassword() {
        return generateRandomToken();
    }

    private String generateRandomToken() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.DIGITS,CharacterPredicates.LETTERS)
                .build();
        return generator.generate(8);
    }
}
