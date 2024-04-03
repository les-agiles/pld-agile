package fr.insa.geofast.controller;

import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class PlanningRequestsController {

    @Setter
    private RightController parentController;

    @FXML
    public CheckBox globalCheckBox;
    @FXML
    public RequestDetailsController requestViewController;

    @FXML
    public Accordion accordion;

    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private Map<String, DeliveryGuy> couriersMap;

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
        updateSelectedDeliveryGuys();
    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        globalCheckBox.setDisable(false);
        globalCheckBox.setSelected(true);

        couriersMap = planningRequest.getCouriersMap(); // Assign the couriersMap

        couriersMap.values()
                .forEach(courier -> {
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
                        updateSelectedDeliveryGuys();
                    });

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
                });
    }

    private void displayRequestInformation(Request request) {
        log.info("Clicked on request: " + request.getId());
    }

    private void updateSelectedDeliveryGuys() {
        if (couriersMap == null) {
            return;
        }
        List<DeliveryGuy> selectedDeliveryGuys = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                String idString = checkBox.getText().replace("Livreur ", "");
                DeliveryGuy deliveryGuy = couriersMap.get(idString);
                if (deliveryGuy != null) {
                    selectedDeliveryGuys.add(deliveryGuy);
                }
            }
        }
        parentController.getParentController().getLeftController().getMapController().displaySelectedDeliveryGuys(selectedDeliveryGuys);
    }

}
