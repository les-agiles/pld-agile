package fr.insa.geofast.controller;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;
import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.GeofastApp;
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

    private final List<MapCircle> intersectionCircles = new ArrayList<>();
    private final List<CoordinateLine> routeLines = new ArrayList<>();
    private final HashMap<String, List<MapLabel>> planningRequestLabels = new HashMap<>();
    private final java.util.Map<DeliveryGuy, List<MapCircle>> deliveryGuyCircles = new java.util.HashMap<>();

    private Marker warehouseMarker = null;

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

    @Getter
    @Setter
    private PlanningRequest planningRequest;

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

        if (!deliveryGuyCircles.isEmpty()) {
            displayPlanningRequest(getPlanningRequest());
        }

        // affichage de la warehouse
        Intersection warehousePosition = map.getWarehouse().getAddress();
        warehouseMarker = new Marker(GeofastApp.class.getResource("warehouse.png"), -10, -10)
                .setPosition(new Coordinate(warehousePosition.getLatitude(), warehousePosition.getLongitude()))
                .setVisible(true);
        mapView.addMarker(warehouseMarker);
    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        deliveryGuyCircles.clear();

        for (Request request : planningRequest.getRequests()) {
            Coordinate coordinate = new Coordinate(request.getDeliveryAddress().getLatitude(), request.getDeliveryAddress().getLongitude());

            MapCircle circle = new MapCircle(coordinate, 10);
            DeliveryGuy deliveryGuy = request.getCourier();
            circle.setColor(deliveryGuy.getColor());
            circle.setVisible(true);

            deliveryGuyCircles.computeIfAbsent(deliveryGuy, k -> new ArrayList<>()).add(circle);
            mapView.addMapCircle(circle);
        }
    }

    public void displayComputedRoutes(PlanningRequest planningRequest) {
        routeLines.forEach(line -> mapView.removeCoordinateLine(line));

        routeLines.clear();

        for (DeliveryGuy courrier : planningRequest.getCouriersMap().values()) {
            CoordinateLine line = getCoordinateLine(courrier);
            line.setVisible(true);
            line.setColor(courrier.getColor());
            mapView.addCoordinateLine(line);
            routeLines.add(line);
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

    public void displaySelectedDeliveryGuys(List<DeliveryGuy> selectedDeliveryGuys) {
        deliveryGuyCircles.forEach((deliveryGuy, circles) -> {
            if (selectedDeliveryGuys.contains(deliveryGuy)) {
                circles.forEach(circle -> mapView.addMapCircle(circle));
            } else {
                circles.forEach(circle -> mapView.removeMapCircle(circle));
            }
        });
    }

    public void setLabelsVisible(String deliveryGuyId, Boolean isVisible) {
        // Si l'ordre des livraison n'a pas encore été calculé, on ne fait rien car on a pas encore de labels à afficher
        if (!planningRequestLabels.containsKey(deliveryGuyId))
            return;

        for (MapLabel label : planningRequestLabels.get(deliveryGuyId)) {
            label.setVisible(isVisible);
        }
    }

    public void updateLabels(PlanningRequest planningRequest) {
        planningRequestLabels.clear();

        for (DeliveryGuy deliveryGuy : planningRequest.getCouriersMap().values()) {
            List<MapLabel> labels = new ArrayList<>();

            for (int i = 0; i < deliveryGuy.getRoute().getRequestsOrdered().size(); i++) {
                Request request = deliveryGuy.getRoute().getRequestsOrdered().get(i);

                Coordinate position = new Coordinate(request.getDeliveryAddress().getLatitude(), request.getDeliveryAddress().getLongitude());

                MapLabel label = new MapLabel(String.valueOf(i)).setPosition(position).setVisible(true);
                labels.add(label);
                mapView.addLabel(label);
            }

            planningRequestLabels.put(deliveryGuy.getId(), labels);
        }
    }
}