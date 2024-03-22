package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;

@Getter
@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {
    @XmlElement(name = "warehouse")
    private Warehouse warehouse;

    @XmlElement(name = "intersection")
    private List<Intersection> intersections;

    @XmlElement(name = "segment")
    private List<Segment> segments;

    private final java.util.Map<String,Intersection> intersectionsMap = new HashMap<>();

    public void setup(){
        intersectionsMap.clear();

        for(Intersection intersection : intersections){
            intersectionsMap.put(intersection.getId(), intersection);
        }
    }
}
