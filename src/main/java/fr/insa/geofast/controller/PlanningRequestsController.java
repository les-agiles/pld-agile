package fr.insa.geofast.controller;

import atlantafx.base.theme.Styles;
import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class PlanningRequestsController {

    private static final String SLIGHTLY_PADDED_CONTENT = "slightly-padded-content";
    private static final String LONGITUDE_LABEL = "long : ";
    private static final String LATITUDE_LABEL = " ; lat : ";

    @Setter
    private RightController parentController;

    @FXML
    private CheckBox globalCheckBox;

    @FXML
    private Accordion accordion;

    @Getter
    private final Map<DeliveryGuy, CheckBox> checkBoxes = new HashMap<>();

    private final Map<String, HBox> requestsTimeHBoxes = new HashMap<>();

    private final Map<String, VBox> requestsVBoxes = new HashMap<>();

    private final Map<String, TitledPane> titledPanes = new HashMap<>();

    private HBox selectedHBox = null;

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
        VBox requestsVBox = new VBox();
        requestsVBoxes.put(courier.getId(), requestsVBox);

        setComputedRequestsForRequestsVBox(requestsVBox, courier.getRoute().getRequests().values());

        titledPane.setContent(requestsVBox);
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

    private void addRequestToVBox(Request request, VBox requestsVBox, boolean isComputed) {
        HBox requestHBox = new HBox();
        requestHBox.setSpacing(30);
        requestHBox.getStyleClass().add(SLIGHTLY_PADDED_CONTENT);

        Label coordinates = new Label(LONGITUDE_LABEL + request.getDeliveryAddress().getLongitude() + LATITUDE_LABEL + request.getDeliveryAddress().getLatitude());
        requestHBox.getChildren().add(coordinates);

        requestHBox.setOnMouseClicked(event -> {
            displayRequestInformation(request);

            if (selectedHBox != requestHBox) {
                parentController.getParentController().getLeftController().getMapController().selectRequest(request.getCourier().getId(), request.getId());
            }

            selectRequest(requestHBox);
        });

        requestHBox.setOnMouseEntered(e -> enterHoverRequest(requestHBox));
        requestHBox.setOnMouseExited(e -> exitHoverRequest(requestHBox));

        requestsVBox.getChildren().add(requestHBox);

        if (isComputed) {
            // Setting arrival time HBoxes
            HBox timeHBox = new HBox();
            timeHBox.setSpacing(5);
            requestsTimeHBoxes.put(request.getId(), timeHBox);
            requestHBox.getChildren().add(timeHBox);
        }
    }

    private void setComputedRequestsForRequestsVBox(VBox requestsVBox, Collection<Request> requests) {
        requests.forEach(request -> addRequestToVBox(request, requestsVBox, true));
    }

    private void selectRequest(HBox requestHBox) {
        if (selectedHBox != null) {
            selectedHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, Insets.EMPTY)));
        }

        if (selectedHBox != requestHBox) {
            selectedHBox = requestHBox;
            requestHBox.setBackground(new Background(new BackgroundFill(Color.VIOLET, new CornerRadii(5), Insets.EMPTY)));
        } else {
            selectedHBox = null;
            parentController.getRequestDetailsController().reset();

            parentController.getParentController().getLeftController().getMapController().unselectRequest();
        }
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
        parentController.getParentController().getLeftController().getMapController().unselectRequest();
    }


    public void refresh(PlanningRequest planningRequest) {
        updateRequestsOrder(planningRequest);
        updateArrivalTimes(planningRequest);
        parentController.getParentController().getLeftController().getMapController().unselectRequest();
    }

    private void clearPlanningRequests() {
        titledPanes.values().forEach(titledPane -> {
            VBox requestsVBox = (VBox) titledPane.getContent();
            requestsVBox.getChildren().clear();
        });
        requestsTimeHBoxes.clear();
    }


    private void updateRequestsOrder(PlanningRequest planningRequest) {
        clearPlanningRequests();

        planningRequest.getCouriersMap().values().forEach(courier -> {
            VBox requestsVBox = new VBox();
            requestsVBoxes.put(courier.getId(), requestsVBox);
            setComputedRequestsForRequestsVBox(requestsVBox, courier.getRoute().getRequestsOrdered());
        });

        List<Request> nonComputedRequests = new ArrayList<>(planningRequest.getCouriersMap().values().stream()
                .flatMap(courier -> courier.getRoute().getRequests().values().stream())
                .filter(request -> request.getArrivalDate() == null)
                .toList());

        Map<String, VBox> nonComputedRequestsVBoxes = new HashMap<>();

        // if nonComputedRequests is not empty, we add the label
        if (!nonComputedRequests.isEmpty()) {
            Label label = new Label("Livraison(s) non assignée(s)");
            label.getStyleClass().addAll(Styles.TEXT, Styles.WARNING, SLIGHTLY_PADDED_CONTENT);

            nonComputedRequests.forEach(request -> {
                VBox vbox = requestsVBoxes.get(request.getCourier().getId());
                requestsVBoxes.put(request.getCourier().getId(), new VBox(vbox, new VBox()));
                VBox nonComputedRequestsVbox = (VBox) requestsVBoxes.get(request.getCourier().getId()).getChildren().get(1);
                nonComputedRequestsVbox.getChildren().add(label);
                nonComputedRequestsVBoxes.put(request.getCourier().getId(), nonComputedRequestsVbox);
            });
        }

        nonComputedRequests.forEach(request -> addRequestToVBox(request, nonComputedRequestsVBoxes.get(request.getCourier().getId()), false));

        titledPanes.forEach((courierId, titledPane) -> {
            VBox requestsVBox = requestsVBoxes.get(courierId);
            titledPane.setContent(requestsVBox);
        });

    }

    private void enterHoverRequest(HBox requestHBox) {
        if (requestHBox != selectedHBox) {
            requestHBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), Insets.EMPTY)));
        }
    }

    private void exitHoverRequest(HBox requestHBox) {
        if (requestHBox != selectedHBox) {
            requestHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, Insets.EMPTY)));
        }
    }

    public void updateArrivalTimes(PlanningRequest planningRequest) {
        requestsTimeHBoxes.values().forEach(timeHBox -> timeHBox.getChildren().clear());

        planningRequest.getCouriersMap().values().forEach(courier -> courier.getRoute().getRequestsOrdered().forEach(request -> {
            Label arrivalTime = new Label(request.getArrivalDate().format(DateTimeFormatter.ofPattern("HH:mm")));
            requestsTimeHBoxes.get(request.getId()).getChildren().add(arrivalTime);

        }));
    }
}
