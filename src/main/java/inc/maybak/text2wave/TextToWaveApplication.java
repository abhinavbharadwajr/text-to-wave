package inc.maybak.text2wave;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import inc.maybak.text2wave.controller.ApplicationController;

@SpringBootApplication
public class TextToWaveApplication extends Application {

    private static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        // Launch JavaFX application
        Application.launch(TextToWaveApplication.class, args);
    }

    @Override
    public void init() {
        // Initialize Spring Boot context
        springContext = new SpringApplicationBuilder(TextToWaveApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/layout.fxml"));
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();
        ApplicationController appController = loader.getController();
        appController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Text to Wave Generator");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
	
}
