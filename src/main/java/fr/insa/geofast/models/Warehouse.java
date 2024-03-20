package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@XmlRootElement(name = "warehouse")
@XmlType(propOrder = {"address"})
public class Warehouse {
    private Intersection address;

    @XmlElement(name = "address")
    public void setAddress(Intersection address) {
        this.address = address;
    }
}
