package fr.insa.geofast.models;

import lombok.Getter;
import java.util.List;

@Getter
public class Map {
    private List<Segment> segments;
    private Warehouse warehouse;
    private List<Intersection> intersections;

}
