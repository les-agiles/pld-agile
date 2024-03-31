package fr.insa.geofast.services;

import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.PlanningRequest;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public class PlanningRequestFactory {
    private PlanningRequestFactory() {
    }

    public static PlanningRequest buildPlanningRequest(String path, Map map) throws JAXBException, FileNotFoundException {
        PlanningRequest result = XMLParser.parsePlanningRequest(path);
        result.setup(map);
        return result;
    }
}
