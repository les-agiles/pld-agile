package fr.insa.geofast.models;

import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.utils.ColorPalette;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@XmlRootElement(name = "planningRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanningRequest {
    @XmlElement(name = "request")
    private List<Request> requests = new ArrayList<>();

    private java.util.Map<String, DeliveryGuy> couriersMap;

    private Warehouse warehouse;

    public void setup(Map map)  throws IHMException {
        warehouse = map.getWarehouse();

        // Create couriers
        couriersMap = new HashMap<>();

        for (Request request : requests) {
            if (!couriersMap.containsKey(request.getCourierId())) {
                couriersMap.put(request.getCourierId(), new DeliveryGuy(request.getCourierId(), warehouse, ColorPalette.getColor(couriersMap.size())));
            }
            // vérification que la requete fait bien partie des intersections du plan qui a été chargé au préalable
            if(map.getIntersectionsMap().get(request.getDeliveryAddressId()) == null)
            {
                String msgErr = "la requete(id: "+request.getId()+") est associée à une intersection(id: "+request.getDeliveryAddress().getId()+") qui n'existe pas dans le plan chargé.";
                throw new IHMException(msgErr);
            }
        }

        int requestCounter = 0;
        // Setup requests
        for (Request request : requests) {
            Intersection deliveryIntersection = map.getIntersectionsMap().get(request.getDeliveryAddressId());
            DeliveryGuy courier = couriersMap.get(request.getCourierId());
            String requestId = deliveryIntersection.getId() + "-" + requestCounter;
            request.setup(deliveryIntersection, courier, requestId);
            requestCounter++;
        }

        // Setup routes
        for (Request request : requests) {
            request.getCourier().getRoute().getRequests().put(request.getId(), request);
        }
    }
}
