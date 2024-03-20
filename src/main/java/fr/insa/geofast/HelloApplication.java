package fr.insa.geofast;

import atlantafx.base.theme.PrimerLight;
import com.sothawo.mapjfx.Projection;
import fr.insa.geofast.controller.MapController;
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

public class HelloApplication extends Application {
    protected static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("GeoFastIcon.png"))));

        String fxmlFile = "map-view.fxml";
        logger.debug("loading fxml file {}", fxmlFile);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        logger.trace("stage loaded");

        final MapController controller = fxmlLoader.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        controller.initMapAndControls(projection);

        Scene scene = new Scene(rootNode);
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