package fr.insa.geofast.models;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.ResponsePath;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.utils.GraphHopperSingleton;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Getter
public class Route {
    // Earliest start of deliveryGuy at the warehouse in seconds (starting at 00:00)
    private static final double EARLIEST_START = 28800;
    // Latest arrival of deliveryGuy at the warehouse in seconds (starting at 00:00)
    private static final double LATEST_ARRIVAL = 43200.;

    private final java.util.Map<String, Request> requests = new HashMap<>();
    private java.util.Map<RelationKey, ResponsePath> intersectionsPathsMatrix = null;
    private List<Request> requestsOrdered = null;
    private final Warehouse warehouse;


    public Route(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * create an ordered list of the requests with arrival time
     */
    public void computeBestRequestsOrder() throws IHMException {
        computeIntersectionsPathsMatrix();
        
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("custom_vehicle_type").setProfile("bike");

        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(warehouse.getAddress().getId()));
        vehicleBuilder.setEarliestStart(EARLIEST_START);
        vehicleBuilder.setLatestArrival(LATEST_ARRIVAL);
        vehicleBuilder.setType(vehicleTypeBuilder.build());
        vehicleBuilder.setReturnToDepot(true);

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicleBuilder.build());
        requests.forEach((id, request) -> vrpBuilder.addJob(request.getJob()));
        vrpBuilder.setRoutingCost(getCostMatrix());

        VehicleRoutingProblem problem = vrpBuilder.build();
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        Optional<VehicleRoute> solution = Solutions.bestOf(solutions).getRoutes().stream().findFirst();

        if(solution.isPresent()) {
            requestsOrdered = new ArrayList<>();

            solution.get().getActivities().forEach(activity -> {
                Request request = requests.get(activity.getLocation().getId());
                request.setArrivalDate(activity.getEndTime() - activity.getOperationTime());
                requestsOrdered.add(request);
            });
        }
        else {
            throw new IHMException("Aucune solution n'a pu être trouvée");
        }
    }

    /**
     * add best paths between all intersections (warehouse included) into intersectionsPathsMatrix
     */
    private void computeIntersectionsPathsMatrix() throws IHMException {
        if(intersectionsPathsMatrix == null) {
            intersectionsPathsMatrix = new HashMap<>();
        }

        try {
            for (Request from : requests.values()) {
                Intersection fromIntersection = from.getDeliveryAddress();
                Intersection warehouseIntersection = warehouse.getAddress();

                intersectionsPathsMatrix.putIfAbsent(RelationKey.newKey(from.getId(), warehouseIntersection.getId()), getPathBetweenIntersections(fromIntersection, warehouseIntersection));
                intersectionsPathsMatrix.putIfAbsent(RelationKey.newKey(warehouseIntersection.getId(), from.getId()), getPathBetweenIntersections(warehouseIntersection, fromIntersection));

                for (Request to : requests.values()) {
                    Intersection toIntersection = to.getDeliveryAddress();
                    intersectionsPathsMatrix.putIfAbsent(RelationKey.newKey(from.getId(), to.getId()), getPathBetweenIntersections(fromIntersection, toIntersection));
                }
            }
        }
        catch (NullPointerException e) {
            log.error(e.getMessage());
            throw new IHMException("Erreur lors du calcul des distances.");
        }
    }

    /**
     * @return best path between two intersections
     */
    private ResponsePath getPathBetweenIntersections(Intersection from, Intersection to) throws IHMException {
        GHRequest req = new GHRequest(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude())
                            .setProfile("bike")
                            .setLocale(Locale.FRENCH);
        GHResponse rsp = GraphHopperSingleton.getInstance().getGraphHopper().route(req);

        if (rsp.hasErrors()) {
            log.error(rsp.getErrors().toString());
            throw new IHMException("Erreur lors du calcul de la tournée.");
        }

        return rsp.getBest();
    }

    /**
     * @return costMatrix that can be used to solve VRP
     */
    private VehicleRoutingTransportCostsMatrix getCostMatrix() {
        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);

        intersectionsPathsMatrix.forEach(((relationKey, responsePath) -> {
            double distance = responsePath.getDistance();
            double time = (double) responsePath.getTime() / 1000;

            matrixBuilder.addTransportTime(relationKey.from, relationKey.to, time);
            matrixBuilder.addTransportDistance(relationKey.from, relationKey.to, distance);
        }));

        return matrixBuilder.build();
    }

    /**
     * Record to use Pairs as keys in intersectionsPathsMatrix
     */
    record RelationKey(String from, String to) {
        static RelationKey newKey(String from, String to) {
            return new RelationKey(from, to);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RelationKey other = (RelationKey) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                return other.to == null;
            } else return to.equals(other.to);
        }
    }
}
