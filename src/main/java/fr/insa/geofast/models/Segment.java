package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;

@Getter
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

}
