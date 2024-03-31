package fr.insa.geofast.controller;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.services.MapFactory;
import fr.insa.geofast.services.PlanningRequestFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class HeaderController implements Initializable {
    @Setter
    private LeftController parentController;

    @FXML
    private Button importerPlan;

    @FXML
    private Button importerProgramme;

    @FXML
    public Button sauvegarderProgramme;

    @FXML
    public Button exporterProgramme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        importerPlan.setOnAction(e -> readMapXml());

        importerProgramme.setOnAction(e -> readPlanningRequestXml());
    }

    private void readMapXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(null);

        try {
            Map map = MapFactory.buildMap(selectedFile.getAbsolutePath());
            parentController.getMapController().displayMap(map);
            parentController.getMapController().setMap(map);
        } catch (IHMException e) {
            parentController.getParentController().displayNotification(e.getMessage());
        }
    }

    private void readPlanningRequestXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(null);

        Map map = parentController.getMapController().getMap();

        try {
            PlanningRequest planningRequest = PlanningRequestFactory.buildPlanningRequest(selectedFile.getAbsolutePath(), map);
            parentController.getMapController().displayPlanningRequest(planningRequest);
            parentController.getMapController().setPlanningRequest(planningRequest);
        } catch (IHMException e) {
            parentController.getParentController().displayNotification(e.getMessage());
        }
    }
}
