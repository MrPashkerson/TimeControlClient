package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/client/clientAuthorization.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 475, 321);
        stage.setTitle("TimeControl Client");
        stage.setOnCloseRequest(windowEvent -> {
            stage.close();
            System.exit(0);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}