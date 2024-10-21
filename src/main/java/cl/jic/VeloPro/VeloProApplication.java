package cl.jic.VeloPro;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
@ComponentScan(basePackages = "cl.jic.VeloPro")
public class VeloProApplication extends Application {

	@Getter private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void init() {
		context = SpringApplication.run(VeloProApplication.class);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/welcome.fxml"));
		fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();

		PauseTransition pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(event -> {
			stage.close();
			showLoginWindow();
		});
		pause.play();
	}

	private void showLoginWindow() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/UserView/login.fxml"));
			fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
			Parent root = fxmlLoader.load();
			Stage loginStage = new Stage();
			loginStage.setScene(new Scene(root));
			loginStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
			loginStage.initStyle(StageStyle.UNDECORATED);
			loginStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		context.close();
	}
}
