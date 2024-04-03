package fr.insa.geofast.controller;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Slf4j
public class GeofastController extends StackPane implements Initializable {
    @FXML
    private LeftController leftController;

    @FXML
    private RightController rightController;

    @FXML
    public StackPane stackPane;

    private Notification errorNotification;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initNotification();
        rightController.setParentController(this);
        leftController.setParentController(this);
    }

    private void initNotification() {
        errorNotification = new Notification(
                "Une erreur est survenue" // default message
        );
        errorNotification.getStyleClass().addAll(
                Styles.DANGER, Styles.ELEVATED_1
        );
        errorNotification.setPrefHeight(Region.USE_PREF_SIZE);
        errorNotification.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(errorNotification, Pos.TOP_RIGHT);
        StackPane.setMargin(errorNotification, new Insets(10, 10, 0, 0));

        errorNotification.setOnClose(e -> {
            var out = Animations.slideOutUp(errorNotification, Duration.millis(250));
            out.setOnFinished(f -> stackPane.getChildren().remove(errorNotification));
            out.playFromStart();
        });
    }

    public void displayNotification(String message) {
        errorNotification.setMessage(message);
        var in = Animations.slideInDown(errorNotification, Duration.millis(250));
        if (!stackPane.getChildren().contains(errorNotification)) {
            stackPane.getChildren().add(errorNotification);
        }
        in.playFromStart();
    }
}
