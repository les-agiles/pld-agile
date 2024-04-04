package fr.insa.geofast.models;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
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
            log.info("DeliveryAddress ID : " + request.getDeliveryAddressId());
            log.info(request.getDeliveryAddress().getLatitude()
                                + "," + request.getDeliveryAddress().getLongitude());
            log.info("Time : " + request.getArrivalDate() + "\n");
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
    void computeBestRequestOrder_testWithMultipleRequestAtSameIntersection() {
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

    @Test
    void computeBestRoute_testOneRequest() {
        Route route = planningRequest.getCouriersMap().get("5").getRoute();

        try {
            route.computeBestRequestsOrder();
            route.computeBestRoute();
        } catch(Exception e) {
            log.debug(e.getMessage());
        }

        List<ResponsePath> bestRoute = route.getBestRoute();

        assertEquals(2, bestRoute.size());

        assertEquals(327929, bestRoute.get(0).getTime());
        assertEquals(1366.35, bestRoute.get(0).getDistance(), 0.01);

        assertEquals(326736, bestRoute.get(1).getTime());
        assertEquals(1361.38, bestRoute.get(1).getDistance(), 0.01);

        double[] latitudesFirstRoute = {
                45.74978947390775,
                45.7497917,
                45.7492136,
                45.7491999,
                45.7492031,
                45.7496968,
                45.7494255,
                45.7493657,
                45.7490234,
                45.7477985,
                45.7489016,
                45.7468363,
                45.747740907234935
        };

        double[] longitudesFirstRoute = {
                4.875719498790989,
                4.8757147,
                4.875591,
                4.8755414,
                4.8754768,
                4.8734548,
                4.8733242,
                4.8733343,
                4.873164,
                4.8724475,
                4.8681255,
                4.8670652,
                4.863443481515568
        };

        PointList firstRoutePoints = bestRoute.get(0).getPoints();
        for (int i = 0; i < firstRoutePoints.size(); i++) {
            assertEquals(latitudesFirstRoute[i], firstRoutePoints.get(i).getLat(), 0.0001);
            assertEquals(longitudesFirstRoute[i], firstRoutePoints.get(i).getLon(), 0.0001);
        }

        double[] latitudesSecondRoute = {
                45.747740907234935,
                45.7476128,
                45.7487122,
                45.747923,
                45.7474938,
                45.7468606,
                45.7490234,
                45.7493657,
                45.7494255,
                45.7496968,
                45.7492031,
                45.7491999,
                45.7492136,
                45.7497917,
                45.74978947390775,
        };

        double[] longitudesSecondRoute = {
                4.863443481515568,
                4.8639398,
                4.8644844,
                4.8676335,
                4.8694178,
                4.8719087,
                4.873164,
                4.8733343,
                4.8733242,
                4.8734548,
                4.8754768,
                4.8755414,
                4.875591,
                4.8757147,
                4.875719498790989,
        };

        PointList secondRoutePoints = bestRoute.get(1).getPoints();
        for (int i = 0; i < secondRoutePoints.size(); i++) {
            assertEquals(latitudesSecondRoute[i], secondRoutePoints.get(i).getLat(), 0.0001);
            assertEquals(longitudesSecondRoute[i], secondRoutePoints.get(i).getLon(), 0.0001);
        }
    }

    @Test
    void computeBestRoute_tesMultipleRequests() {
        Route route = planningRequest.getCouriersMap().get("6").getRoute();

        try {
            route.computeBestRequestsOrder();
            route.computeBestRoute();
        } catch(Exception e) {
            log.debug(e.getMessage());
        }

        assertEquals(5, route.getBestRoute().size());

        List<ResponsePath> bestRoute = route.getBestRoute();
        double[] latitudes = {
                45.74979,
                45.75947,
                45.74773,
                45.75947,
                45.75171,
                45.74979
        };

        double[] longitudes = {
                4.87572,
                4.870945,
                4.8634377,
                4.870945,
                4.8718166,
                4.87572,
        };

        for (int i = 0; i < (bestRoute.size() - 1); i++) {
            assertEquals(latitudes[i], bestRoute.get(i).getWaypoints().getLat(0), 0.1);
            assertEquals(longitudes[i], bestRoute.get(i).getWaypoints().getLon(0), 0.1);

            assertEquals(latitudes[i + 1], bestRoute.get(i).getWaypoints().getLat(1), 0.1);
            assertEquals(longitudes[i + 1], bestRoute.get(i).getWaypoints().getLon(1), 0.1);
        }
    }

    @Test
    void computeBestRoute_testThrowException() {
        Route route = planningRequest.getCouriersMap().get("7").getRoute();
        assertThrows(IHMException.class, route::computeBestRoute);
    }
}
