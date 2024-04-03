package fr.insa.geofast.utils;

import fr.insa.geofast.GeofastApp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IconsHelper {

    private IconsHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String readIcon(String filePath) {
        try {
            // Read SVG path from file
            return Files.readString(Paths.get(GeofastApp.class.getResource(filePath).toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


}
