package cl.jic.VeloPro.Validation;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

@Component
public class ShowingStageValidation {
    public boolean validateStage(String title){
        for (Window window : Stage.getWindows()){
            if (window instanceof Stage && ((Stage)window).getTitle().equals(title)){
                ((Stage) window).toFront();
                return true;
            }
        }
        return false;
    }
}
