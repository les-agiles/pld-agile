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
    private StackPane stackPane;

    private Notification errorNotification;
    private Notification successNotification;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorNotification = new Notification("Une erreur est survenue");
        initNotification(errorNotification, Styles.DANGER);

        successNotification = new Notification("Opération réalisée avec succès");
        initNotification(successNotification, Styles.SUCCESS);

        leftController.setParentController(this);
        rightController.setParentController(this);
    }

    private void initNotification(Notification notification, String style) {
        notification.getStyleClass().addAll(
                style, Styles.ELEVATED_1
        );
        notification.setPrefHeight(Region.USE_PREF_SIZE);
        notification.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(notification, Pos.BOTTOM_LEFT);
        StackPane.setMargin(notification, new Insets(0, 0, 10, 10));

        notification.setOnClose(e -> closeNotification(notification));
    }

    public void displayNotification(String message, String style) {
        Notification notification;

        if (style.equals(Styles.DANGER)) {
            notification = errorNotification;
        } else {
            notification = successNotification;
        }

        notification.setMessage(message);
        var in = Animations.slideInUp(notification, Duration.millis(250));
        if (!stackPane.getChildren().contains(notification)) {
            stackPane.getChildren().add(notification);
        }
        in.playFromStart();

        closeNotificationTimeout(notification);
    }

    private void closeNotificationTimeout(Notification notification) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                closeNotification(notification);
            } catch (InterruptedException ie) {
                log.error("Erreur lors de la fermeture automatique de la notification", ie);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void closeNotification(Notification notification) {
        var out = Animations.slideOutDown(notification, Duration.millis(250));
        out.setOnFinished(f -> stackPane.getChildren().remove(notification));
        out.playFromStart();
    }
}
