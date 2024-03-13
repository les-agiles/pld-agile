module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;


    opens org.example.demo to javafx.fxml;
    exports org.example.demo;
    exports org.example.demo.controller;
    opens org.example.demo.controller to javafx.fxml;
}