package fr.insa.geofast.controller;

import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PlanningRequestsController {

    @Setter
    private RightController parentController;

    @FXML
    private CheckBox globalCheckBox;

    @FXML
    private Accordion accordion;

    @Getter
    private final Map<DeliveryGuy, CheckBox> checkBoxes = new HashMap<>();

    private Map<String, HBox> requestsTimeHBoxes = new HashMap<>();

    private Map<String, VBox> requestsVBoxes = new HashMap<>();

    private Map<String, TitledPane> titledPanes = new HashMap<>();


    public void initialize() {
        // Set the checkbox to be unchecked by default
        globalCheckBox.setDisable(true);

        // Add event listener for globalCheckBox
        globalCheckBox.setOnAction(e -> handleGlobalCheckbox());
    }

    private void handleGlobalCheckbox() {
        log.trace("Global Checkbox clicked");

        if (globalCheckBox.isSelected()) {
            checkBoxes.values().forEach(checkBox -> checkBox.setSelected(true));
        } else {
            checkBoxes.values().forEach(checkBox -> checkBox.setSelected(false));
        }

    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        globalCheckBox.setDisable(false);
        globalCheckBox.setSelected(true);

        resetAccordionPanes();

        Map<String, DeliveryGuy> couriersMap = planningRequest.getCouriersMap(); // Assign the couriersMap

        couriersMap.values().forEach(this::setupCourierAccordion);
    }

    private void setupCourierAccordion(DeliveryGuy courier) {
        CheckBox checkBox = getCheckBox(courier);

        TitledPane titledPane = new TitledPane();
        titledPane.setTextFill(courier.getColor());
        titledPane.setCollapsible(true);

        HBox titledPaneLayout = new HBox(143);

        titledPaneLayout.getChildren().add(checkBox);
        titledPane.setGraphic(titledPaneLayout);

        // Create a VBox to hold the request information
        VBox requestInfoBox = new VBox();
        requestsVBoxes.put(courier.getId(), requestInfoBox);

        setRequestsForRequestInfoBox(requestInfoBox, courier.getRoute().getRequests().values());

        titledPane.setContent(requestInfoBox);
        titledPanes.put(courier.getId(), titledPane);

        accordion.getPanes().add(titledPane);
        checkBoxes.put(courier, checkBox);

        final MapController mapController = parentController.getParentController().getLeftController().getMapController();

        checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) -> {
            mapController.setLabelsVisible(courier.getId(), newVal);
            mapController.setDeliveryPointsVisible(courier.getId(), newVal);
            mapController.setRouteVisible(courier.getId(), newVal);
        });
    }

    private void setRequestsForRequestInfoBox(VBox requestInfoBox, Collection<Request> requests) {
        requests.forEach(request -> {
            HBox requestHBox = new HBox();
            requestHBox.setSpacing(30);

            requestHBox.setStyle("-fx-padding: 10px");

            Label coordinates = new Label("long : " + request.getDeliveryAddress().getLongitude() + " ; lat : " + request.getDeliveryAddress().getLatitude());
            requestHBox.getChildren().add(coordinates);

            requestHBox.setOnMouseClicked(event -> displayRequestInformation(request));
            requestInfoBox.getChildren().add(requestHBox);

            // Setting arrival time HBoxes
            HBox timeHBox = new HBox();
            timeHBox.setSpacing(5);

            requestsTimeHBoxes.put(request.getId(), timeHBox);
            requestHBox.getChildren().add(timeHBox);
        });
    }

    @NotNull
    private CheckBox getCheckBox(DeliveryGuy courier) {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(true);
        checkBox.setText("Livreur " + courier.getId());
        checkBox.setTextFill(courier.getColor());

        checkBox.setOnAction(e -> {
            if (!checkBox.isSelected()) {
                globalCheckBox.setSelected(false);
            } else {
                boolean allSelected = true;

                for (CheckBox cb : checkBoxes.values()) {
                    if (!cb.isSelected()) {
                        allSelected = false;
                        break;
                    }
                }

                if (allSelected) {
                    globalCheckBox.setSelected(true);
                }
            }
        });

        return checkBox;
    }

    private void resetBackground() {
        for (TitledPane titledPane : this.accordion.getPanes()) {
            if (titledPane.getContent() instanceof VBox vbox) {
                for (Node node : vbox.getChildren()) {
                    ((HBox) node).setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                }
            }
        }
    }

    private void displayRequestInformation(Request request) {
        parentController.getRequestDetailsController().updateRequestDetails(request);
    }

    private void resetAccordionPanes() {
        // remove all the panes from the accordion except the first one containing the global checkbox
        accordion.getPanes().remove(1, accordion.getPanes().size());
    }

    public void reset() {
        globalCheckBox.setDisable(true);
        checkBoxes.clear();
        resetAccordionPanes();
    }


    public void refresh(PlanningRequest planningRequest) {
        updateRequestsOrder(planningRequest);
        updateArrivalTimes(planningRequest);
    }

    private void updateRequestsOrder(PlanningRequest planningRequest) {
        // for every titled pane, clear its content
        titledPanes.values().forEach(titledPane -> {
            VBox requestInfoBox = (VBox) titledPane.getContent();
            requestInfoBox.getChildren().clear();
        });
        requestsTimeHBoxes.clear();



        Map<String, VBox> requestsVBoxes = new HashMap<>();

        planningRequest.getCouriersMap().values().forEach(courier -> {
            // we go through the requests of the courier
            VBox requestInfoBox = new VBox();
            requestsVBoxes.put(courier.getId(), requestInfoBox);
            courier.getRoute().getRequestsOrdered().forEach(request -> {
                HBox requestHBox = new HBox();
                requestHBox.setSpacing(30);

                requestHBox.setStyle("-fx-padding: 10px");

                Label coordinates = new Label("long : " + request.getDeliveryAddress().getLongitude() + " ; lat : " + request.getDeliveryAddress().getLatitude());
                requestHBox.getChildren().add(coordinates);

                requestHBox.setOnMouseClicked(event -> displayRequestInformation(request));
                requestInfoBox.getChildren().add(requestHBox);

                // Setting arrival time HBoxes

                HBox timeHBox = new HBox();
                timeHBox.setSpacing(5);

                requestsTimeHBoxes.put(request.getId(), timeHBox);
                requestHBox.getChildren().add(timeHBox);



            });

        });

        titledPanes.forEach((courierId, titledPane) -> {
            VBox requestInfoBox = requestsVBoxes.get(courierId);
            titledPane.setContent(requestInfoBox);
        });

    }

    public void updateArrivalTimes(PlanningRequest planningRequest) {


        requestsTimeHBoxes.values().forEach(timeHBox -> timeHBox.getChildren().clear());

        planningRequest.getCouriersMap().values().forEach(courier -> {
            courier.getRoute().getRequestsOrdered().forEach(request -> {
                Label arrivalTime = new Label(request.getArrivalDate().format(DateTimeFormatter.ofPattern("HH:mm")));

                requestsTimeHBoxes.get(request.getId()).getChildren().add(arrivalTime);

            });
        });

    }
}
