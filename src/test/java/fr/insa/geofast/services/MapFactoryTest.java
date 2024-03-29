package fr.insa.geofast.services;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class MapFactoryTest {
    private static String absolutePath;

    @BeforeAll
    public static void beforeAll() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources", "maps");
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void buildMap_ShouldBuildCorrectMap() {
        Map map = null;

        try {
            map = MapFactory.buildMap(absolutePath + "/unit-tests-map1.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertNotNull(map);
        assertTrue(map.getIntersectionsMap().containsKey("1"));
        assertTrue(map.getIntersectionsMap().containsKey("2"));
        assertEquals(map.getIntersectionsMap().get("1"), map.getIntersections().get(0));
        assertEquals(1, map.getIntersections().get(0).getSegments().size());
        assertEquals(map.getSegments().get(0), map.getIntersections().get(0).getSegments().get(0));
        assertEquals(1, map.getIntersections().get(1).getSegments().size());
        assertEquals(map.getSegments().get(0), map.getIntersections().get(1).getSegments().get(0));
    }

    @Test
    void buildMap_ShouldThrowIHMException() {
        assertThrows(IHMException.class, () -> MapFactory.buildMap(absolutePath + "/unit-tests-map4.xml"));
        assertThrows(IHMException.class, () -> MapFactory.buildMap(absolutePath + "/not-exist.xml"));
    }
}
