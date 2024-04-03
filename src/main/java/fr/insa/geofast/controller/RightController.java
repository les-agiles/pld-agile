package fr.insa.geofast.controller;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.DeliveryGuy;
import fr.insa.geofast.models.PlanningRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Slf4j
public class RightController implements Initializable {
    @Setter
    private GeofastController parentController;

    @FXML
    private PlanningRequestsController planningRequestsController;

    @FXML
    private RequestDetailsController requestDetailsController;

    @FXML
    private Button computeRoutes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	planningRequestsController.setParentController(this);
    	
        computeRoutes.setOnAction(e -> onComputeRoutesPressed());
    }

    private void onComputeRoutesPressed(){
        PlanningRequest planningRequest = parentController.getLeftController().getMapController().getPlanningRequest();

        if(Objects.isNull(planningRequest)){
            parentController.displayNotification("Pas de planning request chargé");
            return;
        }

        try {
            for (DeliveryGuy deliveryGuy : planningRequest.getCouriersMap().values()) {
                deliveryGuy.getRoute().computeBestRequestsOrder();
                deliveryGuy.getRoute().computeBestRoute();
            }
        }catch (IHMException e){
            parentController.displayNotification(e.getMessage());
        }

        parentController.getLeftController().getMapController().displayComputedRoutes(planningRequest);
    }
}