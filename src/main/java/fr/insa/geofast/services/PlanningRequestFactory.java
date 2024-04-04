package fr.insa.geofast.services;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.PlanningRequest;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

@Slf4j
public class PlanningRequestFactory {
    private PlanningRequestFactory() {
    }

    public static PlanningRequest buildPlanningRequest(String path, Map map) throws IHMException {
        if (map == null) {
            log.error("No map found");
            throw new IHMException("Veuillez importer un plan avant de charger un programme");
        }

        PlanningRequest planningRequest;
        try {
            planningRequest = XMLParser.parsePlanningRequest(path);
            planningRequest.setup(map);
        } catch (JAXBException e) {
            log.error(e.getMessage());
            throw new IHMException("Erreur lors de la lecture du fichier XML");
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new IHMException("Fichier introuvable");
        } catch (IHMException e) {
            log.error(e.getMessage());
            throw new IHMException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IHMException("Erreur inconnue");
        }
        return planningRequest;
    }
}
