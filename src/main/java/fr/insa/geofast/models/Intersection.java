package fr.insa.geofast.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "intersection")
@XmlAccessorType(XmlAccessType.FIELD)
public class Intersection {
    @XmlAttribute
    @XmlID
    private String id;

    @XmlAttribute
    private double latitude;

    @XmlAttribute
    private double longitude;

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
