package fr.insa.geofast.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "segment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Segment {
    @XmlAttribute
    private String name;

    @XmlAttribute
    private double length;

    @XmlAttribute
    @XmlIDREF
    private Intersection origin;

    @XmlAttribute
    @XmlIDREF
    private Intersection destination;

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public Intersection getOrigin() {
        return origin;
    }

    public Intersection getDestination() {
        return destination;
    }
}
