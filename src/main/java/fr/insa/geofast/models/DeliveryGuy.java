package fr.insa.geofast.models;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class DeliveryGuy {
  
    private String id;
    private final Route route = new Route();
    private Color color;

    public DeliveryGuy(String id) {
        this.id = id;
        color = Color.RED;
    }

    public DeliveryGuy(String id, Color color) {
        this.id = id;
        this.color = color;
    }
}
