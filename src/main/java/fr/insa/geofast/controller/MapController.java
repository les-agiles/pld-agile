package fr.insa.geofast.controller;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import fr.insa.geofast.models.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    private final List<MapCircle> intersectionCircles = new ArrayList<>();
    private final List<MapCircle> planningRequestCircles = new ArrayList<>();

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

    @Getter
    @Setter
    private Map map;

    public void displayMap(Map map) {
        intersectionCircles.clear();

        for (Intersection intersection : map.getIntersections()) {
            Coordinate coordinate = new Coordinate(intersection.getLatitude(), intersection.getLongitude());

            MapCircle circle = new MapCircle(coordinate, 10);
            circle.setColor(Color.GRAY);
            circle.setVisible(true);

            intersectionCircles.add(circle);
            mapView.addMapCircle(circle);
        }
    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        planningRequestCircles.clear();

        for (Request request : planningRequest.getRequests()) {
            Coordinate coordinate = new Coordinate(request.getDeliveryAddress().getLatitude(), request.getDeliveryAddress().getLongitude());

            MapCircle circle = new MapCircle(coordinate, 10);
            DeliveryGuy deliveryGuy = request.getCourier();
            circle.setColor(deliveryGuy.getColor());
            circle.setVisible(true);

            planningRequestCircles.add(circle);
            mapView.addMapCircle(circle);
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
        // add an event handler for MapViewEvent#MAP_EXTENT and set the extent in the map
        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
            mapView.setExtent(event.getExtent());
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
    }
}