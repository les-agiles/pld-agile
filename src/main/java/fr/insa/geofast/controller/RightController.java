package fr.insa.geofast.controller;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RightController {
    @Setter
    private GeofastController parentController;

    @FXML
    private PlanningRequestsController planningRequestsController;

    @FXML
    private RequestDetailsController requestDetailsController;
}