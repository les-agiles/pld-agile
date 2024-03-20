package fr.insa.geofast.models;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {

    @XmlElement(name = "warehouse")
    private Warehouse warehouse;

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }


}
