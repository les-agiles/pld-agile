package fr.insa.geofast.models;

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

    public void setup(Map map) {
        // Create couriers
        couriersMap = new HashMap<>();

        for (Request request : requests) {
            if (!couriersMap.containsKey(request.getCourierId())) {
                couriersMap.put(request.getCourierId(), new DeliveryGuy(request.getCourierId()));
            }
        }

        // Setup requests
        for (Request request : requests) {
            Intersection deliveryIntersection = map.getIntersectionsMap().get(request.getDeliveryAddressId());
            DeliveryGuy courier = couriersMap.get(request.getCourierId());
            request.setup(deliveryIntersection, courier);
        }
    }

    @Override
    public String toString() {
        return "PlanningRequest{" +
                "requests=" + requests +
                ", couriersMap=" + couriersMap +
                '}';
    }
}
