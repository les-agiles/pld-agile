package fr.insa.geofast.utils;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.CustomModel;
import fr.insa.geofast.Launcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;

import static com.graphhopper.json.Statement.Else;
import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.LIMIT;

@Slf4j
public class GraphHopperSingleton {
    private static final String OSM_FILE_LOC = "fr/insa/geofast/tuile/rhone-alpes.osm.pbf";
    private static final String ROUTE_GRAPH_LOC = "fr/insa/geofast/routing-graph/";

    @Getter
    private static final GraphHopperSingleton instance = new GraphHopperSingleton();
    @Getter
    private final GraphHopper graphHopper;

    private GraphHopperSingleton() {
        CustomModel customModel = new CustomModel().addToSpeed(If("road_class == PRIMARY", LIMIT, "15")).addToSpeed(Else(LIMIT, "15"));
        Profile profile = new Profile("bike").setVehicle("bike").setTurnCosts(false).setCustomModel(customModel);

        Path tempFile = createTempOSMFile();
        Path tempCacheDirectory = createTempCacheDirectory();

        graphHopper = new GraphHopper();
        graphHopper.setOSMFile(tempFile.toString());
        graphHopper.setGraphHopperLocation(tempCacheDirectory.toString());
        graphHopper.setProfiles(profile);
        graphHopper.getCHPreparationHandler().setCHProfiles(new CHProfile("bike"));
        graphHopper.importOrLoad();
    }

    private Path createTempOSMFile() {
        InputStream osmStream = Launcher.class.getClassLoader().getResourceAsStream(OSM_FILE_LOC);
        if (osmStream == null) {
            throw new IllegalStateException("OSM file not found: " + OSM_FILE_LOC);
        }

        try {
            FileAttribute<?> attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---"));
            Path tempFile = Files.createTempFile("rhone-alpes", ".osm.pbf", attrs);
            tempFile.toFile().deleteOnExit();
            try (OutputStream out = Files.newOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = osmStream.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            return tempFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Failed to create temporary file for OSM data", e);
        }
    }

    private Path createTempCacheDirectory() {
        Path tempCacheDirectory;
        try {
            FileAttribute<?> attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---"));
            tempCacheDirectory = Files.createTempDirectory("routing-graph", attrs);
            tempCacheDirectory.toFile().deleteOnExit();
            String[] cacheFiles = {"edgekv_keys", "edgekv_vals", "edges", "geometry", "location_index", "nodes", "nodes_ch_bike", "properties", "shortcuts_bike"};

            for (String cacheFile : cacheFiles) {
                try (InputStream cacheStream = Launcher.class.getClassLoader().getResourceAsStream(ROUTE_GRAPH_LOC + cacheFile)) {
                    if (cacheStream == null) {
                        throw new IllegalStateException("Cache file not found: " + cacheFile);
                    }
                    Path tempCacheFile = tempCacheDirectory.resolve(cacheFile);
                    Files.copy(cacheStream, tempCacheFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return tempCacheDirectory;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Failed to create temporary file for routing-graph data", e);
        }
    }
}