package fr.insa.geofast.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    @Setter
    private Date arrivalDate = null;

    public void setup(Intersection intersection, DeliveryGuy deliveryGuy){
        deliveryAddress = intersection;
        courier = deliveryGuy;
    }
}
