package fr.insa.geofast.controller;

import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
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
    public CheckBox livreursCheckbox;
    @FXML
    public RequestDetailsController requestViewController;

    @FXML
    public Accordion accordion;

    private final List<CheckBox> allCheckBoxes = new ArrayList<>();

    public void initialize() {
        // Set the checkbox to be unchecked by default
        livreursCheckbox.setDisable(true);

        // Add event listener for livreursCheckbox
        livreursCheckbox.setOnAction(e -> handleLivreursCheckbox());
    }

    private void handleLivreursCheckbox() {
        log.trace("Livreurs checkbox clicked");
        if (livreursCheckbox.isSelected()) {
            allCheckBoxes.forEach(checkBox -> checkBox.setSelected(true));
        } else {
            allCheckBoxes.forEach(checkBox -> checkBox.setSelected(false));
        }
    }

    public void displayPlanningRequest(PlanningRequest planningRequest) {
        livreursCheckbox.setDisable(false);
        livreursCheckbox.setSelected(true);

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

                    HBox titledPaneLayout = new HBox(146);

                    SVGPath svgPath = new SVGPath();
                    svgPath.setContent("M8.73335 2.76667H8.23335V3.26667V11.8C8.23335 11.8965 8.20922 11.9874 8.16668 12.0669V11.8V3.26667V2.76667H7.66668H2.33335H1.83335V3.26667V11.8V12.0669C1.7908 11.9874 1.76668 11.8965 1.76668 11.8V3.26667V2.76667H1.26668H0.733346C0.714939 2.76667 0.700012 2.75174 0.700012 2.73333C0.700012 2.71493 0.714939 2.7 0.733346 2.7H2.33335H7.66668H9.26668C9.28507 2.7 9.30001 2.71495 9.30001 2.73333C9.30001 2.75171 9.28507 2.76667 9.26668 2.76667H8.73335ZM7.66668 12.3H7.9336C7.85406 12.3425 7.76319 12.3667 7.66668 12.3667H2.33335C2.23684 12.3667 2.14597 12.3425 2.06644 12.3H2.33335H7.66668ZM2.83335 0.599999C2.83335 0.581592 2.84827 0.566666 2.86668 0.566666H7.13335C7.15175 0.566666 7.16668 0.581592 7.16668 0.599999C7.16668 0.618406 7.15175 0.633332 7.13335 0.633332H2.86668C2.84827 0.633332 2.83335 0.618406 2.83335 0.599999Z");
                    svgPath.setFill(Paint.valueOf("#020817"));
                    svgPath.setStroke(Paint.valueOf("#888888"));

                    titledPaneLayout.getChildren().addAll(checkBox, svgPath);
                    titledPane.setGraphic(titledPaneLayout);

                    // Create a VBox to hold the request information
                    VBox requestInfoBox = new VBox();
                    requestInfoBox.setSpacing(10);


                    courier.getRoute().getRequests().forEach(request -> {
                        Label requestLabel = new Label(LocalTime.of(request.getDeliveryTime(), request.getDeliveryDuration() / 60).format(DateTimeFormatter.ofPattern("HH:mm")));
                        requestInfoBox.getChildren().add(requestLabel);
                    });

                    titledPane.setContent(requestInfoBox);


                    accordion.getPanes().add(titledPane);
                    allCheckBoxes.add(checkBox);
                });
    }

}
