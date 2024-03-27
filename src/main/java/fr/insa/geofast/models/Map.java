package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {
    @XmlElement(name = "warehouse")
    private Warehouse warehouse;

    @XmlElement(name = "intersection")
    private List<Intersection> intersections = new ArrayList<>();

    @XmlElement(name = "segment")
    private List<Segment> segments = new ArrayList<>();

    private final java.util.Map<String, Intersection> intersectionsMap = new HashMap<>();

    public void setup() {
        // init intersection map (dictionary)
        intersectionsMap.clear();

        for (Intersection intersection : intersections) {
            intersectionsMap.put(intersection.getId(), intersection);
        }

        // setup intersection's segments
        for(Segment segment : segments){
            segment.getOrigin().getSegments().add(segment);
            segment.getDestination().getSegments().add(segment);
        }
    }
}
