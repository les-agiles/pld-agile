package fr.insa.geofast.controller;

import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import fr.insa.geofast.utils.IconsHelper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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

    private final Map<String, HBox> requestsHBoxes = new HashMap<>();

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
        requestInfoBox.setSpacing(10);

        courier.getRoute().getRequests().values().forEach(request -> {
            HBox requestHBox = new HBox();
            requestHBox.setSpacing(30);

            Label coordinates = new Label("long : " + request.getDeliveryAddress().getLongitude() + " ; lat : " + request.getDeliveryAddress().getLatitude());
            requestHBox.getChildren().add(coordinates);

            requestHBox.setOnMouseClicked(event -> displayRequestInformation(request));
            requestInfoBox.getChildren().add(requestHBox);
            requestsHBoxes.put(request.getId(), requestHBox);
        });

        titledPane.setContent(requestInfoBox);

        accordion.getPanes().add(titledPane);
        checkBoxes.put(courier, checkBox);

        final MapController mapController = parentController.getParentController().getLeftController().getMapController();

        checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) -> {
            mapController.setLabelsVisible(courier.getId(), newVal);
            mapController.setDeliveryPointsVisible(courier.getId(), newVal);
            mapController.setRouteVisible(courier.getId(), newVal);
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

    public void updateArrivalTimes(PlanningRequest planningRequest) {
        planningRequest.getRequests().forEach(request -> {
            HBox timeHBox = new HBox();

            SVGPath clockIcon = new SVGPath();
            clockIcon.setContent(IconsHelper.readIcon("clock-icon"));

            Label arrivalTime = new Label("HH:mm");

            timeHBox.getChildren().add(arrivalTime);
            requestsHBoxes.get(request.getId()).getChildren().add(timeHBox);
        });
    }
}
