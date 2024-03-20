package fr.insa.geofast.services;

import fr.insa.geofast.models.Intersection;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.Warehouse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class XMLParserTest {
    private static String absolutePath;

    @BeforeAll
    public static void beforeAll() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources");
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void parseMap_ShouldParseTheMostSimpleXMLMap() {
        Map map = null;

        try {
            map = XMLParser.parseMap(absolutePath + "/unit-tests-map1.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(map);
        assertNotNull(map.getWarehouse().getAddress());
        assertNotNull(map.getIntersections());
        assertEquals(2, map.getIntersections().size());
        assertEquals("1", map.getIntersections().get(0).getId());
        assertEquals(map.getIntersections().get(0), map.getWarehouse().getAddress());
        assertSame(map.getIntersections().get(0), map.getWarehouse().getAddress());
        assertEquals(1, map.getSegments().size());
        assertEquals(map.getIntersections().get(0), map.getSegments().get(0).getOrigin());
        assertEquals(map.getIntersections().get(1), map.getSegments().get(0).getDestination());
    }

    @Test
    void parseMap_ShouldParseASmallSizedXMLMap(){
        Map map = null;

        try {
            map = XMLParser.parseMap(absolutePath + "/unit-tests-map2.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(map);
        assertEquals(308, map.getIntersections().size());
        assertEquals(616, map.getSegments().size());
    }
}
