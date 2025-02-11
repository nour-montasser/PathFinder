module tn.esprit.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens tn.esprit.demo to javafx.fxml;
    exports tn.esprit.demo;
    exports tn.esprit.demo.Controller to javafx.fxml;
    opens tn.esprit.demo.Controller to javafx.fxml;

}