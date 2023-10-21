package de.mfroese.wplieferanten;

import de.mfroese.wplieferanten.settings.AppTexts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// junit

// todo Programmicon

public class main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 820, 600);

        stage.setMinWidth(820);
        stage.setMinHeight(600);
        stage.setTitle(AppTexts.APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }
}