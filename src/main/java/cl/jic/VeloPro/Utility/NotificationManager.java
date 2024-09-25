package cl.jic.VeloPro.Utility;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.stereotype.Component;

@Component
public class NotificationManager {

    public void successNotification(String title, String text, Pos pos){
        Notifications.create()
                .title(title)
                .text(text)
                .darkStyle()
                .position(pos)
                .hideAfter(Duration.seconds(3))
                .showConfirm();
    }

    public void errorNotification(String title, String text, Pos pos){
        Notifications.create()
                .title(title)
                .text(text)
                .darkStyle()
                .position(pos)
                .hideAfter(Duration.seconds(3))
                .showError();
    }

    public void informationNotification(String title, String text, Pos pos){
        Notifications.create()
                .title(title)
                .text(text)
                .darkStyle()
                .position(pos)
                .hideAfter(Duration.seconds(3))
                .showInformation();
    }

    public void warningNotification(String title, String text, Pos pos){
        Notifications.create()
                .title(title)
                .text(text)
                .darkStyle()
                .position(pos)
                .hideAfter(Duration.seconds(3))
                .showWarning();
    }
}
