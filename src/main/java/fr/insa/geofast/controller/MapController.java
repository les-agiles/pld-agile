package fr.insa.geofast.controller;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;
import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import fr.insa.geofast.GeofastApp;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.*;
import fr.insa.geofast.utils.ColorPalette;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;

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

    private Marker warehouseMarker = null;
    private final java.util.Map<String, CoordinateLine> routeLines = new HashMap<>();
    private final java.util.Map<String, List<MapLabel>> planningRequestLabels = new HashMap<>();
    private final java.util.Map<String, List<Marker>> deliveryGuyMarkers = new HashMap<>();

    @Setter
    private LeftController parentController;

    /**
     * button to set the map's zoom.
     */
    @FXML
    private Button zoomButton;

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
    private Map map;

    @Getter
    private PlanningRequest planningRequest;

    public void displayMap(Map map) {
        if (!Objects.isNull(warehouseMarker)) {
            mapView.removeMarker(warehouseMarker);
        }

        this.map = map;

        Intersection warehouse = map.getWarehouse().getAddress();
        Coordinate warehouseCoords = new Coordinate(warehouse.getLatitude(), warehouse.getLongitude());

        // Affichage de la warehouse
        warehouseMarker = new Marker(Objects.requireNonNull(GeofastApp.class.getResource("warehouse.png")), -10, -10)
                .setPosition(warehouseCoords)
                .setVisible(true);
        mapView.addMarker(warehouseMarker);

        if (!deliveryGuyMarkers.isEmpty()) {
            displayPlanningRequest(getPlanningRequest());
        }

        mapView.setCenter(warehouseCoords);
    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        deliveryGuyMarkers.clear();
        for (DeliveryGuy deliveryGuy : planningRequest.getCouriersMap().values()) {
            List<Marker> markers = new ArrayList<>();
            for (Request request : deliveryGuy.getRoute().getRequests().values()) {
                Coordinate coordinate = new Coordinate(request.getDeliveryAddress().getLatitude(), request.getDeliveryAddress().getLongitude());

                var deliveryGuyColor = deliveryGuy.getColor();

                var deliveryGuyColorName = "RED";

                try {
                    deliveryGuyColorName = ColorPalette.getColorName(deliveryGuyColor).toUpperCase();
                } catch (Exception e) {
                    log.error("Error getting deliveryGuyColor", e);
                }

                Marker marker = new Marker(Objects.requireNonNull(GeofastApp.class.getResource("markers/" + deliveryGuyColorName + ".png")), -10, -20);
                marker.setPosition(coordinate);
                marker.setVisible(true);

                markers.add(marker);
                mapView.addMarker(marker);
            }
            deliveryGuyMarkers.put(deliveryGuy.getId(), markers);
        }

        this.planningRequest = planningRequest;
    }

    public void displayComputedRoutes(PlanningRequest planningRequest) {
        clearRouteLines();
        java.util.Map<DeliveryGuy, CheckBox> checkBoxes = parentController.getParentController().getRightController().getPlanningRequestsController().getCheckBoxes();

        for (DeliveryGuy courrier : planningRequest.getCouriersMap().values()) {
            CoordinateLine line = getCoordinateLine(courrier);
            // If the courrier's checkbox is selected, we display the route
            line.setVisible(checkBoxes.get(courrier).isSelected());
            line.setColor(courrier.getColor());
            mapView.addCoordinateLine(line);
            routeLines.put(courrier.getId(), line);
        }
    }

    @NotNull
    private static CoordinateLine getCoordinateLine(DeliveryGuy courrier) {
        List<Coordinate> coords = new ArrayList<>();

        for (ResponsePath path : courrier.getRoute().getBestRoute()) {
            PointList points = path.getPoints();

            for (int i = 0; i < points.size(); i++) {
                GHPoint3D point = points.get(i);
                Coordinate coord = new Coordinate(point.getLat(), point.getLon());
                coords.add(coord);
            }
        }

        return new CoordinateLine(coords);
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
        zoomButton.setOnAction(event -> mapView.setZoom(ZOOM_DEFAULT));
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

    public void setLabelsVisible(String deliveryGuyId, Boolean isVisible) {
        // Si l'ordre des livraison n'a pas encore été calculé, on ne fait rien car on a pas encore de labels à afficher
        if (!planningRequestLabels.containsKey(deliveryGuyId))
            return;

        for (MapLabel label : planningRequestLabels.get(deliveryGuyId)) {
            label.setVisible(isVisible);
        }
    }

    public void setDeliveryPointsVisible(String deliveryGuyId, Boolean isVisible) {
        if (!deliveryGuyMarkers.containsKey(deliveryGuyId)) {
            return;
        }

        if (Boolean.TRUE.equals(isVisible)) {
            deliveryGuyMarkers.get(deliveryGuyId).forEach(mapView::addMarker);
        } else {
            deliveryGuyMarkers.get(deliveryGuyId).forEach(mapView::removeMarker);
        }
    }

    public void setRouteVisible(String id, Boolean isVisible) {
        if (!routeLines.containsKey(id)) {
            return;
        }

        routeLines.get(id).setVisible(isVisible);
    }

    public void updateLabels(PlanningRequest planningRequest) {
        planningRequestLabels.values().forEach(labels -> labels.forEach(mapView::removeLabel));

        java.util.Map<DeliveryGuy, CheckBox> checkBoxes = parentController.getParentController().getRightController().getPlanningRequestsController().getCheckBoxes();

        for (DeliveryGuy deliveryGuy : planningRequest.getCouriersMap().values()) {
            List<MapLabel> labels = new ArrayList<>();

            for (int i = 0; i < deliveryGuy.getRoute().getRequestsOrdered().size(); i++) {
                Request request = deliveryGuy.getRoute().getRequestsOrdered().get(i);

                Coordinate position = new Coordinate(request.getDeliveryAddress().getLatitude(), request.getDeliveryAddress().getLongitude());

                MapLabel label = new MapLabel(String.valueOf(i + 1)).setPosition(position)
                        // if the checkbox is selected, we display the label
                        .setVisible(checkBoxes.get(deliveryGuy).isSelected());

                labels.add(label);
                mapView.addLabel(label);
            }

            planningRequestLabels.put(deliveryGuy.getId(), labels);
        }
    }

    private void clearRouteLines() {
        routeLines.values().forEach(line -> mapView.removeCoordinateLine(line));
        routeLines.clear();
    }

    public void resetMapPlanningRequest() {
        if (!Objects.isNull(planningRequest)) {
            planningRequest.getCouriersMap().values().forEach(deliveryGuy -> {
                setDeliveryPointsVisible(deliveryGuy.getId(), false);
                setLabelsVisible(deliveryGuy.getId(), false);
            });
            planningRequest = null;

            clearRouteLines();

            deliveryGuyMarkers.clear();
            planningRequestLabels.clear();
        }
    }

    public void reset() {
        resetMapPlanningRequest();
        warehouseMarker = null;
    }
}