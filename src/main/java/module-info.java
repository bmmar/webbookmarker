module com.brian.exceltotableview {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.poi.poi;
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires java.prefs;


    opens com.brian.webbookmarker to javafx.fxml;
    exports com.brian.webbookmarker;
    exports com.brian.webbookmarker.controllers;
    opens com.brian.webbookmarker.controllers to javafx.fxml;
    exports com.brian.webbookmarker.models;
    opens com.brian.webbookmarker.models to javafx.fxml;
}