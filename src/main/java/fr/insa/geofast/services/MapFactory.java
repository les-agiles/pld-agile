package fr.insa.geofast.services;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.Map;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

@Slf4j
public class MapFactory {
    private MapFactory() { }

    public static Map buildMap(String path) throws IHMException {
        Map map;

        try {
            map = XMLParser.parseMap(path);
        } catch (JAXBException e) {
            log.error(e.getMessage());
            throw new IHMException("Erreur lors de la lecture du fichier XML");
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new IHMException("Fichier introuvable");
        }

        map.setup();

        return map;
    }
}
