package fr.insa.geofast.controller;

import fr.insa.geofast.models.Request;
import fr.insa.geofast.models.Segment;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RequestDetailsController implements Initializable {

    @FXML
    private Label latValue;

    @FXML
    private Label lonValue;

    @FXML
    private Label deliveryRange;

    @FXML
    private Label deliveryTime;

    @FXML
    private Label deliveryGuy;

    @FXML
    private HBox rowHeureDePassage;

    @FXML
    private VBox streets;

    @FXML
    private VBox details;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        details.setVisible(false);
    }

    public void updateRequestDetails(Request request){

        details.setVisible(true);

        this.latValue.setText(Double.toString(request.getDeliveryAddress().getLatitude()));

        this.lonValue.setText(Double.toString(request.getDeliveryAddress().getLongitude()));

        this.deliveryRange.setText(String.format("%dh-%dh", request.getDeliveryTime(), request.getDeliveryTime()+1));
        this.deliveryGuy.setText(request.getCourier().getId());

        this.streets.getChildren().clear();

        for (Segment segment : request.getDeliveryAddress().getSegments())
        {
            Label street = new Label();
            street.setText(segment.getName());
            this.streets.getChildren().add(street);
        }

        if (request.getArrivalDate() !=  0)
        {
            rowHeureDePassage.setVisible(true);
            this.deliveryTime.setText(String.format("%d:%d", (int)request.getArrivalDate(), (int)(request.getArrivalDate()) + 1));
        }
        else
        {
            rowHeureDePassage.setVisible(false);
        }
    }
}
