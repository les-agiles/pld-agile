package fr.insa.geofast.controller;

import fr.insa.geofast.models.Request;
import fr.insa.geofast.models.Segment;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RequestDetailsController implements Initializable {
    @FXML
    private Label latValue;

    @FXML
    private Label lonValue;

    @FXML
    private Label timeLabel;

    @FXML
    private Label timeValue;

    @FXML
    private Label deliveryGuy;

    @FXML
    private VBox streets;

    @FXML
    private VBox details;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reset();
    }

    public void updateRequestDetails(Request request){

        details.setVisible(true);

        this.latValue.setText(Double.toString(request.getDeliveryAddress().getLatitude()));
        this.lonValue.setText(Double.toString(request.getDeliveryAddress().getLongitude()));

        if (request.getArrivalDate() != 0) // calculation not done
        {
            this.timeLabel.setText("Heure de passage");

            int arrivalHour = (int)(request.getArrivalDate()/3600);
            int arrivalMinutes = (int)((request.getArrivalDate() - arrivalHour*3600)/60);
            this.timeValue.setText(String.format("%d:%d", arrivalHour, arrivalMinutes));
        }
        else
        {
            this.timeValue.setText(String.format("%dh-%dh", request.getDeliveryTime(), request.getDeliveryTime()+1));
        }

        this.deliveryGuy.setText(request.getCourier().getId());

        this.streets.getChildren().clear();

        for (Segment segment : request.getDeliveryAddress().getSegments()) {
            Label street = new Label();
            street.setText(segment.getName());
            this.streets.getChildren().add(street);
        }

        if (request.getArrivalDate() != null) {
            rowArrivalDate.setVisible(true);
            this.deliveryTime.setText(String.format("%d:%d", request.getArrivalDate().toSecondOfDay(), request.getArrivalDate().toSecondOfDay() + 1));
        } else {
            rowArrivalDate.setVisible(false);
        }
    }

    public void reset() {
        details.setVisible(false);
    }
}