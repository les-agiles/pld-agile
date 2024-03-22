package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;

@Getter
@XmlRootElement(name = "warehouse")
@XmlAccessorType(XmlAccessType.FIELD)
public class Warehouse {
    @XmlAttribute
    @XmlIDREF
    private Intersection address;

}
