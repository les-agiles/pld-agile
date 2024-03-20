package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;


@XmlRootElement(name = "warehouse")
@XmlAccessorType(XmlAccessType.FIELD)
public class Warehouse {
    @XmlAttribute
    private long address;

    public void setAddress(long address) {
        this.address = address;
    }

    public long getAddress() {
        return address;
    }
}
