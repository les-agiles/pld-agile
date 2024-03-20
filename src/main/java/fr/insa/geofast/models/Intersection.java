package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "intersection")
@XmlAccessorType(XmlAccessType.FIELD)
public class Intersection {
    @XmlAttribute
    private long id;
    @XmlAttribute
    private double latitude;
    @XmlAttribute
    private double longitude;

    public void setId(long id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
