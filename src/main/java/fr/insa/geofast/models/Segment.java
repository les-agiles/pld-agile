package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@XmlRootElement(name = "segment")
@XmlType(propOrder = {"destination", "length", "name", "origin"})
public class Segment {
    private String name;
    private double length;
    private Intersection destination;
    private Intersection origin;

    //Setter for all
    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "length")
    public void setLength(double length) {
        this.length = length;
    }

    @XmlElement(name = "destination")
    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    @XmlElement(name = "origin")
    public void setOrigin(Intersection origin) {
        this.origin = origin;
    }
}
