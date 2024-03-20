package fr.insa.geofast.models;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {
    @XmlElement(name = "warehouse")
    private Warehouse warehouse;

    @XmlElement(name = "intersection")
    private List<Intersection> intersections;

    @XmlElement(name = "segment")
    private List<Segment> segments;

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<Segment> getSegments(){
        return segments;
    }
}
