package fr.insa.geofast.services;

import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.PlanningRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class XMLParser {
    private XMLParser(){ }

    public static Map parseMap(String path) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Map.class);
        return (Map) jaxbContext.createUnmarshaller().unmarshal(new FileReader(path));
    }

    public static PlanningRequest parsePlanningRequest(String path) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PlanningRequest.class);
        return (PlanningRequest) jaxbContext.createUnmarshaller().unmarshal(new FileReader(path));
    }
}
