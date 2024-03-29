package fr.insa.geofast.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class DeliveryGuy {
    private String id;
    private final Route route = new Route();

    public DeliveryGuy(String id){
        this.id = id;
    }
}
