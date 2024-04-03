package fr.insa.geofast.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class RightController implements Initializable {
    @Setter
    private GeofastController parentController;

    @FXML
    private PlanningRequestsController planningRequestsController;

    @FXML
    private RequestDetailsController requestDetailsController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        planningRequestsController.setParentController(this);
    }
}