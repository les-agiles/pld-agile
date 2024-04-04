package fr.insa.geofast.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public final class LeftController implements Initializable {
    @Setter
    private GeofastController parentController;

    @FXML
    private HeaderController headerController;

    @FXML
    private MapController mapController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerController.setParentController(this);
    }
}
