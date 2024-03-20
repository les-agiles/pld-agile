module fr.insa.geofast {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires lombok;
    requires java.xml.bind;


    opens fr.insa.geofast to javafx.fxml;
    exports fr.insa.geofast;
    exports fr.insa.geofast.controller;
    opens fr.insa.geofast.controller to javafx.fxml;
}