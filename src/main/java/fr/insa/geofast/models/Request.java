package fr.insa.geofast.models;

import com.graphhopper.jsprit.core.problem.AbstractJob;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.time.LocalTime;

@Getter
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Request {
    // Interval in hours starting at deliveryTime
    private static final double TIME_WINDOW_INTERVAL = 1.;

    private DeliveryGuy courier;

    @XmlAttribute(name = "courier")
    private String courierId;

    private Intersection deliveryAddress;

    private String id;

    @XmlAttribute(name = "deliveryAddress")
    private String deliveryAddressId;

    @XmlAttribute
    private int deliveryDuration;
    @XmlAttribute
    private int deliveryTime;

    @Setter
    private LocalTime arrivalDate;

    public void setup(Intersection intersection, DeliveryGuy deliveryGuy, String id){
        deliveryAddress = intersection;
        courier = deliveryGuy;
        this.id = id;
    }

    public AbstractJob getJob() {
        double timeWindowLowerBound = (double) deliveryTime * 3600;
        double timeWindowUpperBound = timeWindowLowerBound + (TIME_WINDOW_INTERVAL * 3600);

        Service.Builder<Service> sBuilder = Service.Builder.newInstance(id);
        sBuilder.setLocation(Location.newInstance(id));
        sBuilder.setServiceTime(deliveryDuration);
        sBuilder.setTimeWindow(new TimeWindow(timeWindowLowerBound, timeWindowUpperBound));

        return sBuilder.build();
    }
}
