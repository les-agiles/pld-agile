package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Getter
@XmlRootElement(name = "intersection")
@XmlType(propOrder = {"id", "latitude", "longitude"})
public class Intersection {
    private String id;
    private double latitude;
    private double longitude;
    private List<Segment> segments;

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "latitude")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @XmlElement(name = "longitude")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
