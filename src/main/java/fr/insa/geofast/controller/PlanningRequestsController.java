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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.javafx.FontIcon;

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

                    HBox titledPaneLayout = new HBox(143);

                    var outerIcon1 = new FontIcon("mdal-delete_outline");

                    titledPaneLayout.getChildren().addAll(checkBox, outerIcon1);
                    titledPane.setGraphic(titledPaneLayout);

                    // Create a VBox to hold the request information
                    VBox requestInfoBox = new VBox();
                    requestInfoBox.setSpacing(10);

                    courier.getRoute().getRequests().values().forEach(request -> {
                        Label requestLabel = new Label(LocalTime.of(request.getDeliveryTime(), request.getDeliveryDuration() / 60).format(DateTimeFormatter.ofPattern("HH:mm")));
                        requestInfoBox.getChildren().add(requestLabel);
                    });

                    titledPane.setContent(requestInfoBox);

                    accordion.getPanes().add(titledPane);
                    allCheckBoxes.add(checkBox);
                });
    }

}
