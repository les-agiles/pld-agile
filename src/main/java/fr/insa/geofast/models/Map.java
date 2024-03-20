package fr.insa.geofast.models;


import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {

    @XmlElement(name = "warehouse")
    private Warehouse warehouse;

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}
