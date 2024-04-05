package fr.insa.geofast.controller;

import atlantafx.base.theme.Styles;
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
    private Button computeRoutesButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        planningRequestsController.setParentController(this);

        computeRoutesButton.setOnAction(e -> onComputeRoutesPressed());
    }

    private void onComputeRoutesPressed() {
        MapController mapController = parentController.getLeftController().getMapController();
        PlanningRequest planningRequest = mapController.getPlanningRequest();

        if (Objects.isNull(planningRequest)) {
            parentController.displayNotification("Pas de programme de livraison chargé", Styles.DANGER);
            return;
        }

        try {
            for (DeliveryGuy deliveryGuy : planningRequest.getCouriersMap().values()) {
                deliveryGuy.getRoute().computeBestRequestsOrder();
                deliveryGuy.getRoute().computeBestRoute();
            }
        } catch (IHMException e) {
            parentController.displayNotification(e.getMessage(), Styles.DANGER);
        }

        // Le calcul a réussi
        parentController.displayNotification("Tournées calculées avec succès", Styles.SUCCESS);
        planningRequestsController.refresh(planningRequest);
        mapController.updateLabels(planningRequest);
        mapController.displayComputedRoutes(planningRequest);
        parentController.getLeftController().getHeaderController().setExportButtonVisible(true);
    }

    public void reset() {
        planningRequestsController.reset();
        requestDetailsController.reset();
    }
}