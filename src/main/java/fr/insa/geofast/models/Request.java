package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;

@Getter
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Request {
    private DeliveryGuy courier;

    @XmlAttribute(name = "courier")
    private String courierId;

    private Intersection deliveryAddress;

    @XmlAttribute(name = "deliveryAddress")
    private String deliveryAddressId;

    @XmlAttribute
    private int deliveryDuration;
    @XmlAttribute
    private int deliveryTime;

    public void setup(Intersection intersection, DeliveryGuy deliveryGuy){

    }
}
