package fr.insa.geofast.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public final class LeftController implements Initializable {
    @FXML
    private HeaderController headerController;

    @FXML
    private MapController mapController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
