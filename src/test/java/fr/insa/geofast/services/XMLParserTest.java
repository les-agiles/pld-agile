package fr.insa.geofast.services;

import fr.insa.geofast.models.Intersection;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.Warehouse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class XMLParserTest {
    private static String absolutePath;

    @BeforeAll
    public static void setUp() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources");
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void testParseMap() {
        Map map = null;
        try {
            map = XMLParser.parseMap(absolutePath + "/unit-tests-map1.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertNotNull(map);
        Assertions.assertEquals(1, map.getWarehouse().getAddress());
    }
}
