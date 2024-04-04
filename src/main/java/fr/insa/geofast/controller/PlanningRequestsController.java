package fr.insa.geofast.controller;

import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    private final List<CheckBox> checkBoxes = new ArrayList<>();

    public void initialize() {
        // Set the checkbox to be unchecked by default
        globalCheckBox.setDisable(true);

        // Add event listener for globalCheckBox
        globalCheckBox.setOnAction(e -> handleGlobalCheckbox());
    }

    private void handleGlobalCheckbox() {
        log.trace("Global Checkbox clicked");

        if (globalCheckBox.isSelected()) {
            checkBoxes.forEach(checkBox -> checkBox.setSelected(true));
        } else {
            checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
        }

    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        globalCheckBox.setDisable(false);
        globalCheckBox.setSelected(true);

        // remove all the panes from the accordion except the first one containing the global checkbox
        accordion.getPanes().remove(1, accordion.getPanes().size());

        Map<String, DeliveryGuy> couriersMap = planningRequest.getCouriersMap(); // Assign the couriersMap

        couriersMap.values().forEach(this::setupCourierAccodion);
    }

    private void setupCourierAccodion(DeliveryGuy courier) {
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

            Label coordinates = new Label("x : " + request.getDeliveryAddress().getLongitude() + " ; y : " + request.getDeliveryAddress().getLatitude());
            requestHBox.getChildren().add(coordinates);

            requestHBox.setOnMouseClicked(event -> displayRequestInformation(request));
            requestInfoBox.getChildren().add(requestHBox);

        });

        titledPane.setContent(requestInfoBox);

        accordion.getPanes().add(titledPane);
        checkBoxes.add(checkBox);

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

                for (CheckBox cb : checkBoxes) {
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
}
