package fr.insa.geofast.controller;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import fr.insa.geofast.services.XMLParser;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

@Slf4j
public class MapController implements Initializable {
    /**
     * some coordinates from around town.
     */
    private static final Coordinate mapCenterCoords = new Coordinate(45.75, 4.85);

    /**
     * default zoom value.
     */
    private static final int ZOOM_DEFAULT = 14;

    private final ArrayList<Marker> intersections = new ArrayList<>();

    /**
     * button to set the map's zoom.
     */
    @FXML
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

    private void loadAndDisplayMap() {
        try {
            URL mapXmlUrl = getClass().getResource("/smallMap.xml");
            File mapXmlFile = new File(Objects.requireNonNull(mapXmlUrl).toURI());
            var map = XMLParser.parseMap(mapXmlFile.getAbsolutePath());

            for (var intersection : map.getIntersections()) {
                Coordinate coordinate = new Coordinate(intersection.getLatitude(), intersection.getLongitude());
                var marker = Marker.createProvided(Marker.Provided.BLUE)
                        .setPosition(coordinate)
                        .setVisible(true);

                intersections.add(marker);
                mapView.addMarker(marker);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("begin initialize");

        // set the custom css file for the MapView
        mapView.setCustomMapviewCssURL(Objects.requireNonNull(getClass().getResource("/custom_mapview.css")));

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
            if (Boolean.TRUE.equals(newValue)) {
                afterMapIsInitialized();
            }
        });

        setupEventHandlers();

        // finally initialize the map view
        log.trace("start map initialization");
        mapView.initialize(Configuration.builder()
                .projection(Projection.WEB_MERCATOR)
                .showZoomControls(false)
                .build());
        log.debug("initialization finished");
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

        mapView.addEventHandler(
            MapViewEvent.MAP_POINTER_MOVED,
            event -> log.debug("pointer moved to " + event.getCoordinate())
        );

        log.trace("map handlers initialized");
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

        // start at the center coords with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(mapCenterCoords);

        // now enable the controls
        setControlsDisable(false);

        loadAndDisplayMap();
    }
}