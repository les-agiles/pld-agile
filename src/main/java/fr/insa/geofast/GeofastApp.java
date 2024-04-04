package fr.insa.geofast;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class GeofastApp extends Application {
    protected static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(getClass().getResource("theme/cupertino-light.css").toString());

        stage.getIcons().add(new Image(Objects.requireNonNull(GeofastApp.class.getResourceAsStream("GeoFastIcon.png"))));

        String fxmlFile = "geofast-view.fxml";
        logger.debug("loading fxml file {}", fxmlFile);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent rootNode = fxmlLoader.load();
        logger.trace("stage loaded");

        Scene scene = new Scene(rootNode, 1000, 700);
        logger.trace("scene created");

        stage.setTitle("GeoFast!");
        stage.setScene(scene);
        logger.trace("showing scene");
        stage.show();

        logger.debug("application start method finished.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}