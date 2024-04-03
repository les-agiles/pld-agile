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
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        java.util.Map<String, DeliveryGuy> couriersMap = planningRequest.getCouriersMap();

        couriersMap.values()
                .forEach(courier -> {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(true);
                    checkBox.setText("Livreur " + courier.getId());
                    checkBox.setTextFill(courier.getColor());

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

                        var timeIcon = new SVGPath();
                        timeIcon.setContent("M7.3667 7.98621V7.7791V4.80001C7.3667 4.7816 7.35177 4.76667 7.33336 4.76667C7.31496 4.76667 7.30003 4.7816 7.30003 4.80001V8.00001C7.30003 8.00885 7.30354 8.01733 7.3098 8.02358L9.4431 10.1569C9.44311 10.1569 9.44312 10.1569 9.44313 10.1569C9.45614 10.1699 9.47724 10.1699 9.49025 10.1569C9.49026 10.1569 9.49028 10.1569 9.49029 10.1569M7.3667 7.98621L9.84381 9.75622C10.0521 9.9645 10.0521 10.3022 9.84381 10.5105L9.49029 10.1569M7.3667 7.98621L7.51314 8.13265L9.49025 10.1098M7.3667 7.98621L9.49025 10.1098M9.49029 10.1569C9.50327 10.1439 9.50326 10.1228 9.49026 10.1098C9.49026 10.1098 9.49026 10.1098 9.49025 10.1098M9.49029 10.1569L9.49025 10.1098M0.769104 7.99983C0.769104 4.37444 3.70807 1.43548 7.33346 1.43548C10.9588 1.43548 13.8978 4.37444 13.8978 7.99983C13.8978 11.6252 10.9589 14.5642 7.33346 14.5642C3.70807 14.5642 0.769104 11.6252 0.769104 7.99983ZM7.33346 1.44881C3.71544 1.44881 0.782439 4.38181 0.782439 7.99983C0.782439 11.6178 3.71543 14.5509 7.33346 14.5509C10.9515 14.5509 13.8845 11.6178 13.8845 7.99983C13.8845 4.38181 10.9515 1.44881 7.33346 1.44881Z");
                        timeIcon.setFill(Paint.valueOf("#020817"));
                        timeIcon.setStroke(Paint.valueOf("#020817"));

                        Label deliveryTime = new Label(LocalTime.of(request.getDeliveryTime(), request.getDeliveryDuration() / 60).format(DateTimeFormatter.ofPattern("HH:mm")));

                        var timeHBox = new HBox();
                        timeHBox.setSpacing(5);

                        timeHBox.getChildren().addAll(timeIcon, deliveryTime);
                        requestHBox.getChildren().add(timeHBox);

                        requestHBox.setOnMouseClicked(event -> displayRequestInformation(request));
                        requestInfoBox.getChildren().add(requestHBox);

                    });

                    titledPane.setContent(requestInfoBox);

                    accordion.getPanes().add(titledPane);
                    checkBoxes.add(checkBox);
                });
    }

    private void displayRequestInformation(Request request) {
        System.out.println("Clicked on request: " + request.getId());
    }

}
