package fr.insa.geofast.services;

import fr.insa.geofast.models.PlanningRequest;
import fr.insa.geofast.models.Request;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class XMLPlanningRequestParserTest {
    private static String absolutePath;

    @BeforeAll
    public static void beforeAll() {
        Path resourceDirectory = Paths.get("src", "test", "java", "resources", "requests");
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Test
    void parsePlanningRequest_ShouldParseTheMostSimpleXMLMap() {
        PlanningRequest planningRequest = null;

        try {
            planningRequest = XMLParser.parsePlanningRequest(absolutePath + "/unit-tests-request1.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        assertNotNull(planningRequest);
        assertEquals(2, planningRequest.getRequests().size());
        assertEquals("1", planningRequest.getRequests().get(0).getCourierId());
        assertEquals("1", planningRequest.getRequests().get(0).getDeliveryAddressId());
        assertEquals(540, planningRequest.getRequests().get(0).getDeliveryDuration());
        assertEquals(8, planningRequest.getRequests().get(0).getDeliveryTime());
    }

    @Test
    void parsePlanningRequest_ShouldParseASmallSizedXMLMap() {
        PlanningRequest planningRequest = null;

        try {
            planningRequest = XMLParser.parsePlanningRequest(absolutePath + "/unit-tests-request2.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        assertNotNull(planningRequest);
        assertEquals(11, planningRequest.getRequests().size());
        assertEquals("1", planningRequest.getRequests().get(0).getCourierId());
        assertEquals("55444215", planningRequest.getRequests().get(0).getDeliveryAddressId());
        assertEquals(480, planningRequest.getRequests().get(0).getDeliveryDuration());
        assertEquals(9, planningRequest.getRequests().get(0).getDeliveryTime());

        assertEquals(11, planningRequest.getRequests().size());
        assertEquals("2", planningRequest.getRequests().get(10).getCourierId());
        assertEquals("26317393", planningRequest.getRequests().get(10).getDeliveryAddressId());
        assertEquals(420, planningRequest.getRequests().get(10).getDeliveryDuration());
        assertEquals(8, planningRequest.getRequests().get(10).getDeliveryTime());
    }

    @Test
    void parsePlanningRequest_ShouldNotWork_DueIncorrectXMLDataFormat() {
        assertThrows(Exception.class, () -> XMLParser.parseMap(absolutePath + "/unit-tests-request4.xml"));
    }

    @Test
    void parsePlanningRequest_ShouldParseEmptyPlanningRequest() {
        PlanningRequest planningRequest = null;

        try {
            planningRequest = XMLParser.parsePlanningRequest(absolutePath + "/unit-tests-request4.xml");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        assertNotNull(planningRequest);
        assertEquals(0, planningRequest.getRequests().size());
        PlanningRequest finalPlanningRequest = planningRequest;
        List<Request> requests = finalPlanningRequest.getRequests();
        assertThrows(IndexOutOfBoundsException.class, () -> requests.get(0));
    }
}
