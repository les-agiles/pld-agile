package fr.insa.geofast.controller;

import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import fr.insa.geofast.utils.IconsHelper;
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

                        timeIcon.setContent(IconsHelper.readIcon("clock-icon"));
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
