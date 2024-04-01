package fr.insa.geofast.models;

import com.graphhopper.reader.osm.Pair;
import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.services.MapFactory;
import fr.insa.geofast.services.PlanningRequestFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class RouteTest {
    private static PlanningRequest planningRequest;

    @BeforeAll
    public static void beforeAll() {
        String testResourcesPath = "src/test/java/resources/";
        try{
            Map map = MapFactory.buildMap(testResourcesPath + "maps/unit-tests-map5.xml");
            planningRequest = PlanningRequestFactory.buildPlanningRequest(testResourcesPath + "requests/unit-tests-request5.xml", map);
        }
        catch(Exception e){
            log.debug(e.getMessage());
        }
    }

    @Test
    void computeBestRequestOrder() {
        Route route = planningRequest.getCouriersMap().get("1").getRoute();

        try {
            route.computeBestRequestsOrder();
        }
        catch (Exception e) {
            log.debug(e.getMessage());
        }

        List<Request> requestsOrdered = route.getRequestsOrdered();

        assertNotNull(requestsOrdered);

        requestsOrdered.forEach(request -> {
            System.out.println("DeliveryAddress ID : " + request.getDeliveryAddressId());
            System.out.println(request.getDeliveryAddress().getLatitude()
                                + "," + request.getDeliveryAddress().getLongitude());
            System.out.println("Time : " + request.getArrivalDate() + "\n");
        });
    }

    @Test
    void computeBestRequestOrder_testWithoutTimeConstraint() {
        Route route = planningRequest.getCouriersMap().get("1").getRoute();

        try {
            route.computeBestRequestsOrder();
        } catch(Exception e) {
            log.debug(e.getMessage());
        }

        List<Request> requestsOrdered = route.getRequestsOrdered();

        String[] ids = {"2129259176", "1288817934", "21992995", "1036842078", "208769083"};
        double[] times = {32400, 32989, 33738, 34264, 34697};

        assertNotNull(requestsOrdered);
        assertEquals(ids.length, requestsOrdered.size());

        for(int i = 0; i < requestsOrdered.size(); i++) {
            assertEquals(ids[i], requestsOrdered.get(i).getDeliveryAddressId());
            assertEquals(times[i], requestsOrdered.get(i).getArrivalDate(), 0.99);
        }
    }

    @Test
    void computeBestRequestOrder_testWithTimeConstraint() {
        Route route = planningRequest.getCouriersMap().get("2").getRoute();

        try {
            route.computeBestRequestsOrder();
        } catch(Exception e) {
            log.debug(e.getMessage());
        }

        List<Request> requestsOrdered = route.getRequestsOrdered();

        String[] ids = {"208769083", "21992995", "1036842078", "2129259176", "1288817934"};
        double[] times = {32400, 33136, 36000, 36365, 39600};

        assertNotNull(requestsOrdered);
        assertEquals(ids.length, requestsOrdered.size());

        for(int i = 0; i < requestsOrdered.size(); i++) {
            assertEquals(ids[i], requestsOrdered.get(i).getDeliveryAddressId());
            assertEquals(times[i], requestsOrdered.get(i).getArrivalDate(), 0.99);
        }
    }

    @Test
    void computeBestRequestOrder_testWithMultipleRequestAtSameIntersection() throws IHMException {
        Route route = planningRequest.getCouriersMap().get("3").getRoute();

        try {
            route.computeBestRequestsOrder();
        } catch(Exception e) {
            log.debug(e.getMessage());
        }

        List<Request> requestsOrdered = route.getRequestsOrdered();

        String[] ids = {"21992995", "208769083", "208769083", "2129259176", "1288817934"};
        double[] times = {32400, 36000, 36000, 36581, 39600};

        assertNotNull(requestsOrdered);
        assertEquals(ids.length, requestsOrdered.size());

        for(int i = 0; i < requestsOrdered.size(); i++) {
            assertEquals(ids[i], requestsOrdered.get(i).getDeliveryAddressId());
            assertEquals(times[i], requestsOrdered.get(i).getArrivalDate(), 0.99);
        }
    }

    @Test
    void computeBestRequestOrder_testNoSolution() {
        Route route = planningRequest.getCouriersMap().get("4").getRoute();
        assertThrows(IHMException.class, route::computeBestRequestsOrder);
    }
}
