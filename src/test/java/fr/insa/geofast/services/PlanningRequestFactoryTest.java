package fr.insa.geofast.services;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.PlanningRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PlanningRequestFactoryTest {
    private static String mapAbsolutePath, planningRequestAbsolutePath;

    @BeforeAll
    public static void beforeAll() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources", "maps");
        mapAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
        resourceDirectory = Paths.get("src", "test", "java", "resources", "requests");
        planningRequestAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void buildPlanningRequest_ShouldBuildCorrectPlanningRequest() {
        Map map = null;
        PlanningRequest planningRequest = null;

        try {
            map = MapFactory.buildMap(mapAbsolutePath + "/unit-tests-map1.xml");
            planningRequest = PlanningRequestFactory.buildPlanningRequest(planningRequestAbsolutePath + "/unit-tests-request1.xml", map);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        assertNotNull(planningRequest);
        assertEquals(1, planningRequest.getCouriersMap().size());
        assertTrue(planningRequest.getCouriersMap().containsKey("1"));
        assertEquals(map.getIntersections().get(0), planningRequest.getRequests().get(0).getDeliveryAddress());
        assertEquals(planningRequest.getCouriersMap().get("1"), planningRequest.getRequests().get(0).getCourier());
        assertEquals(planningRequest.getRequests().get(0).getCourier(), planningRequest.getRequests().get(1).getCourier());
        assertEquals("1", planningRequest.getRequests().get(0).getCourier().getId());
        assertEquals(map.getWarehouse(), planningRequest.getWarehouse());
        assertNotNull(planningRequest.getCouriersMap().get("1").getRoute());
        assertEquals(0, planningRequest.getRequests().get(0).getArrivalDate(), 0.1);
        assertEquals(2, planningRequest.getCouriersMap().get("1").getRoute().getRequests().size());
    }

    @Test
    void buildPlanningRequest_ShouldThrowIHMExceptionBecauseMapIsNull(){
        assertThrows(IHMException.class, () -> PlanningRequestFactory.buildPlanningRequest(planningRequestAbsolutePath + "/unit-tests-request1.xml", null));
    }

    @Test
    void buildPlanningRequest_ShouldThrowIHMExceptionBecauseParsingError(){
        Map map = null;

        try {
            map = MapFactory.buildMap(mapAbsolutePath + "/unit-tests-map1.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        Map finalMap = map;
        IHMException ex =  assertThrows(IHMException.class, () -> PlanningRequestFactory.buildPlanningRequest(planningRequestAbsolutePath + "/unit-tests-request3.xml", finalMap));
        assertEquals("Erreur lors de la lecture du fichier XML", ex.getMessage());

        ex = assertThrows(IHMException.class, () -> PlanningRequestFactory.buildPlanningRequest(planningRequestAbsolutePath + "/not-exist.xml", finalMap));
        assertEquals("Fichier introuvable", ex.getMessage());
    }
}
