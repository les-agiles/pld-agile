package fr.insa.geofast.models;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class DeliveryGuy {
    private final String id;
    private final Route route;
    private final Color color;

    public DeliveryGuy(String id, Warehouse warehouse) {
        this.id = id;
        this.route = new Route(warehouse);
        color = Color.RED;
    }

    public DeliveryGuy(String id, Warehouse warehouse, Color color) {
        this.id = id;
        this.route = new Route(warehouse);
        this.color = color;
    }
}
