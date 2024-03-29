package fr.insa.geofast.services;

import fr.insa.geofast.models.Map;
import fr.insa.geofast.models.PlanningRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

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
    void buildPlanningRequest_ShouldBuildCorrectPlanningRequest(){
        Map map = null;
        PlanningRequest planningRequest = null;

        try{
            map = MapFactory.buildMap(mapAbsolutePath + "/unit-tests-map1.xml");
            planningRequest = PlanningRequestFactory.buildPlanningRequest(planningRequestAbsolutePath + "/unit-tests-request1.xml", map);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        System.out.println(planningRequest);

        assertNotNull(planningRequest);
        assertEquals(1, planningRequest.getCouriersMap().size());
        assertTrue(planningRequest.getCouriersMap().containsKey("1"));
        assertEquals(map.getIntersections().get(0), planningRequest.getRequests().get(0).getDeliveryAddress());
        assertEquals(planningRequest.getCouriersMap().get("1"), planningRequest.getRequests().get(0).getCourier());
        assertEquals(planningRequest.getRequests().get(0).getCourier(), planningRequest.getRequests().get(1).getCourier());
        assertEquals("1", planningRequest.getRequests().get(0).getCourier().getId());
    }
}
