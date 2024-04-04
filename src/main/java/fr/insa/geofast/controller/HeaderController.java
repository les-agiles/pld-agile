package fr.insa.geofast.controller;

import atlantafx.base.theme.Styles;
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
    private Button importMapButton;

    @FXML
    private Button importPlanningRequestButton;

    @FXML
    private Button exportToPDFButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        importMapButton.setOnAction(e -> readMapXml());

        importPlanningRequestButton.setOnAction(e -> readPlanningRequestXml());
        importPlanningRequestButton.setVisible(false);
        exportToPDFButton.setVisible(false);
    }

    private void readMapXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile == null) {
            log.info("FileChooser opened but no file selected");
            return;
        }

        try {
            Map map = MapFactory.buildMap(selectedFile.getAbsolutePath());
            parentController.getMapController().displayMap(map);
            importPlanningRequestButton.setVisible(true);

            parentController.getParentController().displayNotification("Plan importé avec succès", Styles.SUCCESS);
        } catch (IHMException e) {
            parentController.getParentController().displayNotification(e.getMessage(), Styles.DANGER);
        }

    }

    private void readPlanningRequestXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile == null) {
            log.info("FileChooser opened but no file selected");
            return;
        }

        Map map = parentController.getMapController().getMap();

        try {
            PlanningRequest planningRequest = PlanningRequestFactory.buildPlanningRequest(selectedFile.getAbsolutePath(), map);
            parentController.getParentController().getRightController().getPlanningRequestsController().displayPlanningRequest(planningRequest);

            parentController.getMapController().displayPlanningRequest(planningRequest);
            
            parentController.getParentController().displayNotification("Programme importé avec succès", Styles.SUCCESS);
        } catch (IHMException e) {
            parentController.getParentController().displayNotification(e.getMessage(), Styles.DANGER);
        }
    }

    public void setExportButtonVisible(boolean visible) {
        exportToPDFButton.setVisible(visible);
    }
}
