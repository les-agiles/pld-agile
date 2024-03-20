package fr.insa.geofast.models;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "warehouse")
@XmlAccessorType(XmlAccessType.FIELD)
public class Warehouse {
    @XmlAttribute
    @XmlIDREF
    private Intersection address;

    public Intersection getAddress() {
        return address;
    }
}
