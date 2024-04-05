package fr.insa.geofast.utils;

import fr.insa.geofast.GeofastApp;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
public class IconsHelper {

    private IconsHelper() { }

    public static SVGPath getIcon(String filePath, Paint strokeColor, Paint fillColor) {
        SVGPath icon = new SVGPath();

        try {
            icon.setContent(Files.readString(Paths.get(Objects.requireNonNull(GeofastApp.class.getResource(filePath)).toURI())));
        } catch (IOException | URISyntaxException | NullPointerException e) {
            log.error(e.getMessage());
            return null;
        }

        icon.setStroke(strokeColor);
        icon.setFill(fillColor);
        return icon;
    }
}
