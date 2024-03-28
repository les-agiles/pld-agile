package fr.insa.geofast.controller;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import fr.insa.geofast.services.XMLParser;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MapController implements Initializable {
    /**
     * some coordinates from around town.
     */
    private static final Coordinate coordKarlsruheCastle = new Coordinate(49.013517, 8.404435);
    private static final Coordinate coordKarlsruheHarbour = new Coordinate(49.015511, 8.323497);
    private static final Coordinate coordKarlsruheStation = new Coordinate(48.993284, 8.402186);
    private static final Coordinate coordKarlsruheSoccer = new Coordinate(49.020035, 8.412975);
    private static final Coordinate coordKarlsruheUniversity = new Coordinate(49.011809, 8.413639);

    /**
     * default zoom value.
     */
    private static final int ZOOM_DEFAULT = 14;

    /**
     * the markers.
     */
    private final Marker markerKaHarbour;
    private final Marker markerKaCastle;
    private final Marker markerKaStation;
    private final Marker markerKaSoccer;
    private final Marker markerClick;

    /**
     * the labels.
     */
    private final MapLabel labelKaUniversity;
    private final MapLabel labelKaCastle;
    private final MapLabel labelKaStation;
    private final MapLabel labelClick;
    private final ArrayList<Marker> intersections = new ArrayList<>();

    // a circle around the castle
    private final MapCircle circleCastle;

    @FXML
    /** button to set the map's zoom. */
    private Button buttonZoom;

    /**
     * the MapView containing the map
     */
    @FXML
    private MapView mapView;

    /**
     * the box containing the top controls, must be enabled when mapView is initialized
     */
    @FXML
    private HBox topControls;

    /**
     * Slider to change the zoom value
     */
    @FXML
    private Slider sliderZoom;

    /**
     * Label to display the current center
     */
    @FXML
    private Label labelCenter;

    /**
     * Label to display the current extent
     */
    @FXML
    private Label labelExtent;

    /**
     * Label to display the current zoom
     */
    @FXML
    private Label labelZoom;

    /**
     * label to display the last event.
     */
    @FXML
    private Label labelEvent;



    /**
     * the first CoordinateLine
     */
    private CoordinateLine trackMagenta;

    /**
     * the second CoordinateLine
     */
    private CoordinateLine trackCyan;

    /**
     * Coordinateline for polygon drawing.
     */
    private CoordinateLine polygonLine;
    /**
     * Check Button for polygon drawing mode.
     */
    @FXML
    private CheckBox checkDrawPolygon;

    /**
     * params for the WMS server.
     */
    private final WMSParam wmsParam = new WMSParam()
            .setUrl("http://ows.terrestris.de/osm/service?")
            .addParam("layers", "OSM-WMS");

    private final XYZParam xyzParams = new XYZParam()
            .withUrl("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x})")
            .withAttributions(
                    "'Tiles &copy; <a href=\"https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer\">ArcGIS</a>'");

    public MapController() {
        // a couple of markers using the provided ones
        markerKaHarbour = Marker.createProvided(Marker.Provided.BLUE).setPosition(coordKarlsruheHarbour).setVisible(
                false);
        markerKaCastle = Marker.createProvided(Marker.Provided.GREEN).setPosition(coordKarlsruheCastle).setVisible(
                false);
        markerKaStation =
                Marker.createProvided(Marker.Provided.RED).setPosition(coordKarlsruheStation).setVisible(false);
        // no position for click marker yet
        markerClick = Marker.createProvided(Marker.Provided.ORANGE).setVisible(false);

        // a marker with a custom icon
        markerKaSoccer = new Marker(getClass().getResource("/ksc.png"), -20, -20).setPosition(coordKarlsruheSoccer)
                .setVisible(false);

        // the fix label, default style
        labelKaUniversity = new MapLabel("university").setPosition(coordKarlsruheUniversity).setVisible(true);
        // the attached labels, custom style
        labelKaCastle = new MapLabel("castle", 10, -10).setVisible(false).setCssClass("green-label");
        labelKaStation = new MapLabel("station", 10, -10).setVisible(false).setCssClass("red-label");
        labelClick = new MapLabel("click!", 10, -10).setVisible(false).setCssClass("orange-label");

        markerKaCastle.attachLabel(labelKaCastle);
        markerKaStation.attachLabel(labelKaStation);
        markerClick.attachLabel(labelClick);

        circleCastle = new MapCircle(coordKarlsruheStation, 1_000).setVisible(true);
    }

    private void loadAndDisplayMap() {
        try {
            URL test = getClass().getResource("/smallMap.xml");
            var map = XMLParser.parseMap(new File(test.toURI()).getAbsolutePath());

            for (var intersection : map.getIntersections()) {
                Coordinate coordinate = new Coordinate(intersection.getLatitude(), intersection.getLongitude());
                var marker = Marker.createProvided(Marker.Provided.BLUE)
                        .setPosition(coordinate)
                        .setVisible(true);

                intersections.add(marker);
                mapView.addMarker(marker);
            }
        } catch (FileNotFoundException | JAXBException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("begin initialize");

        // init MapView-Cache
        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
//        logger.info("using dir for cache: " + cacheDir);
//        try {
//            Files.createDirectories(Paths.get(cacheDir));
//            offlineCache.setCacheDirectory(cacheDir);
//            offlineCache.setActive(true);
//        } catch (IOException e) {
//            logger.warn("could not activate offline cache", e);
//        }

        // set the custom css file for the MapView
        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));

        // set the controls to disabled, this will be changed when the MapView is intialized
        setControlsDisable(true);

        // wire the zoom button and connect the slider to the map's zoom
        buttonZoom.setOnAction(event -> mapView.setZoom(ZOOM_DEFAULT));
        sliderZoom.valueProperty().bindBidirectional(mapView.zoomProperty());

        // bind the map's center and zoom properties to the corresponding labels and format them
        labelCenter.textProperty().bind(Bindings.format("center: %s", mapView.centerProperty()));
        labelZoom.textProperty().bind(Bindings.format("zoom: %.0f", mapView.zoomProperty()));
        log.trace("options and labels done");

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        setupEventHandlers();

        // load two coordinate lines
        trackMagenta = loadCoordinateLine(getClass().getResource("/M1.csv")).orElse(new CoordinateLine
                ()).setColor(Color.MAGENTA);
        trackCyan = loadCoordinateLine(getClass().getResource("/M2.csv")).orElse(new CoordinateLine
                ()).setColor(Color.CYAN).setWidth(7);
        log.trace("tracks loaded");
        // get the extent of both tracks
        Extent tracksExtent = Extent.forCoordinates(
                Stream.concat(trackMagenta.getCoordinateStream(), trackCyan.getCoordinateStream())
                        .collect(Collectors.toList()));
        ChangeListener<Boolean> trackVisibleListener =
                (observable, oldValue, newValue) -> mapView.setExtent(tracksExtent);
        trackMagenta.visibleProperty().addListener(trackVisibleListener);
        trackCyan.visibleProperty().addListener(trackVisibleListener);

        // finally initialize the map view
        log.trace("start map initialization");
        mapView.initialize(Configuration.builder()
                .projection(Projection.WEB_MERCATOR)
                .showZoomControls(false)
                .build());
        log.debug("initialization finished");

        long animationStart = System.nanoTime();
        new AnimationTimer() {
            @Override
            public void handle(long nanoSecondsNow) {
                if (markerKaSoccer.getVisible()) {
                    // every 100ms, increase the rotation of the markerKaSoccer by 9 degrees, make a turn in 4 seconds
                    long milliSecondsDelta = (nanoSecondsNow - animationStart) / 1_000_000;
                    long numSteps = milliSecondsDelta / 100;
                    int angle = (int) ((numSteps * 9) % 360);
                    if (markerKaSoccer.getRotation() != angle) {
                        markerKaSoccer.setRotation(angle);
                    }
                }
            }
        }.start();
    }

    /**
     * initializes the event handlers.
     */
    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            labelEvent.setText("Event: map clicked at: " + newPosition);
            if (checkDrawPolygon.isSelected()) {
                handlePolygonClick(event);
            }
            if (markerClick.getVisible()) {
                final Coordinate oldPosition = markerClick.getPosition();
                if (oldPosition != null) {
                    animateClickMarker(oldPosition, newPosition);
                } else {
                    markerClick.setPosition(newPosition);
                    // adding can only be done after coordinate is set
                    mapView.addMarker(markerClick);
                }
            }
        });

        // add an event handler for MapViewEvent#MAP_EXTENT and set the extent in the map
        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
            mapView.setExtent(event.getExtent());
        });

        // add an event handler for extent changes and display them in the status label
        mapView.addEventHandler(MapViewEvent.MAP_BOUNDING_EXTENT, event -> {
            event.consume();
            labelExtent.setText(event.getExtent().toString());
        });

        mapView.addEventHandler(MapViewEvent.MAP_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: map right clicked at: " + event.getCoordinate());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker clicked: " + event.getMarker().getId());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker right clicked: " + event.getMarker().getId());
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label clicked: " + event.getMapLabel().getText());
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label right clicked: " + event.getMapLabel().getText());
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, event -> {
            log.debug("pointer moved to " + event.getCoordinate());
        });

        log.trace("map handlers initialized");
    }

    private void animateClickMarker(Coordinate oldPosition, Coordinate newPosition) {
        // animate the marker to the new position
        final Transition transition = new Transition() {
            private final Double oldPositionLongitude = oldPosition.getLongitude();
            private final Double oldPositionLatitude = oldPosition.getLatitude();
            private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
            private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

            {
                setCycleDuration(Duration.seconds(1.0));
                setOnFinished(evt -> markerClick.setPosition(newPosition));
            }

            @Override
            protected void interpolate(double v) {
                final double latitude = oldPosition.getLatitude() + v * deltaLatitude;
                final double longitude = oldPosition.getLongitude() + v * deltaLongitude;
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        };
        transition.play();
    }

    /**
     * shows a new polygon with the coordinate from the added.
     *
     * @param event event with coordinates
     */
    private void handlePolygonClick(MapViewEvent event) {
        final List<Coordinate> coordinates = new ArrayList<>();
        if (polygonLine != null) {
            polygonLine.getCoordinateStream().forEach(coordinates::add);
            mapView.removeCoordinateLine(polygonLine);
            polygonLine = null;
        }
        coordinates.add(event.getCoordinate());
        polygonLine = new CoordinateLine(coordinates)
                .setColor(Color.DODGERBLUE)
                .setFillColor(Color.web("lawngreen", 0.4))
                .setClosed(true);
        mapView.addCoordinateLine(polygonLine);
        polygonLine.setVisible(true);
    }

    /**
     * enables / disables the different controls
     *
     * @param flag if true the controls are disabled
     */
    private void setControlsDisable(boolean flag) {
        topControls.setDisable(flag);
    }

    /**
     * finishes setup after the mpa is initialzed
     */
    private void afterMapIsInitialized() {
        log.trace("map intialized");
        log.debug("setting center and enabling controls...");
        // start at the harbour with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordKarlsruheHarbour);
        // add the markers to the map - they are still invisible
        mapView.addMarker(markerKaHarbour);
        mapView.addMarker(markerKaCastle);
        mapView.addMarker(markerKaStation);
        mapView.addMarker(markerKaSoccer);
        // can't add the markerClick at this moment, it has no position, so it would not be added to the map

        // add the fix label, the other's are attached to markers.
        mapView.addLabel(labelKaUniversity);

        // add the tracks
        mapView.addCoordinateLine(trackMagenta);
        mapView.addCoordinateLine(trackCyan);

        // add the circle
        mapView.addMapCircle(circleCastle);

        // now enable the controls
        setControlsDisable(false);

        loadAndDisplayMap();
    }

    /**
     * load a coordinateLine from the given uri in lat;lon csv format
     *
     * @param url url where to load from
     * @return optional CoordinateLine object
     * @throws java.lang.NullPointerException if uri is null
     */
    private Optional<CoordinateLine> loadCoordinateLine(URL url) {
        try (
                Stream<String> lines = new BufferedReader(
                        new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)).lines()
        ) {
            return Optional.of(new CoordinateLine(
                    lines.map(line -> line.split(";")).filter(array -> array.length == 2)
                            .map(values -> new Coordinate(Double.valueOf(values[0]), Double.valueOf(values[1])))
                            .collect(Collectors.toList())));
        } catch (IOException | NumberFormatException e) {
            log.error("load {}", url, e);
        }
        return Optional.empty();
    }
}