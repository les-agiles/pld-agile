package fr.insa.geofast.services;

import fr.insa.geofast.models.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class XMLMapParserTest {
    private static String absolutePath;

    @BeforeAll
    public static void beforeAll() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources", "maps");
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void parseMap_ShouldParseTheMostSimpleXMLMap() {
        Map map = null;

        try {
            map = XMLParser.parseMap(absolutePath + "/unit-tests-map1.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
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
        assertEquals(45.75406, map.getIntersections().get(0).getLatitude());
        assertEquals(4.857418, map.getIntersections().get(0).getLongitude());
        assertEquals("Rue Test", map.getSegments().get(0).getName());
        assertEquals(69.69, map.getSegments().get(0).getLength());
    }

    @Test
    void parseMap_ShouldParseASmallSizedXMLMap() {
        Map map = null;

        try {
            map = XMLParser.parseMap(absolutePath + "/unit-tests-map2.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        assertNotNull(map);
        assertEquals(308, map.getIntersections().size());
        assertEquals(616, map.getSegments().size());
    }

    @Test
    void parseMap_WharehouseAdressShouldBeNull_DueToNonExistingIntersectionId() {
        Map map = null;

        try {
            map = XMLParser.parseMap(absolutePath + "/unit-tests-map3.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        assertNotNull(map);
        assertNull(map.getWarehouse().getAddress());
    }

    @Test
    void parseMap_ShouldNotWork_DueIncorrectXMLDataFormat() {
        assertThrows(Exception.class, () -> XMLParser.parseMap(absolutePath + "/unit-tests-map4.xml"));
    }
}
