package fr.insa.geofast.models;

import lombok.Getter;
import java.util.List;

@Getter
public class Intersection {
    private String id;
    private double latitude;
    private double longitude;
    private List<Segment> segments;
}
