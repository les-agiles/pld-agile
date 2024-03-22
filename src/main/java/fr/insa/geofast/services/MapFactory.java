package fr.insa.geofast.services;

import fr.insa.geofast.models.Map;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public class MapFactory {
    private MapFactory(){ }

    public static Map buildMap(String path) throws JAXBException, FileNotFoundException {
        Map result = XMLParser.parseMap(path);
        result.setup();
        return result;
    }
}
