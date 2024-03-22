package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;

@Getter
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

}