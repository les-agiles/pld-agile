package fr.insa.geofast.controller;

import javafx.fxml.FXML;
import lombok.Getter;

@Getter
public final class LeftController {
    @FXML
    private HeaderController headerController;

    @FXML
    private MapController mapController;
}
