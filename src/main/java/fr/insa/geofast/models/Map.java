package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {
    @XmlElement(name = "segment")
    private List<Segment> segments;

    @XmlElement(name = "warehouse")
    private Warehouse warehouse;

    @XmlElement(name = "intersection")
    private List<Intersection> intersections;

}
