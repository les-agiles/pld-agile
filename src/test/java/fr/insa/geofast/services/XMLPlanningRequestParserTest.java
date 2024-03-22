package fr.insa.geofast.services;

import fr.insa.geofast.models.PlanningRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class XMLPlanningRequestParserTest {
    private static String absolutePath;

    @BeforeAll
    public static void beforeAll() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources", "requests");
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void parsePlanningRequest_ShouldParseTheMostSimpleXMLMap(){
        PlanningRequest planningRequest = null;

        try {
            planningRequest = XMLParser.parsePlanningRequest(absolutePath + "/unit-tests-request1.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(planningRequest);
        assertEquals(2, planningRequest.getRequests().size());
        assertEquals("1", planningRequest.getRequests().get(0).getCourierId());
        assertEquals("1", planningRequest.getRequests().get(0).getDeliveryAddressId());
        assertEquals(540, planningRequest.getRequests().get(0).getDeliveryDuration());
        assertEquals(8, planningRequest.getRequests().get(0).getDeliveryTime());
    }
}
