module client.timecontrolclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    opens client to javafx.fxml;
    exports client;
    exports client.mvc;
    opens client.mvc to javafx.fxml;
    exports client.mvc.observer;
    opens client.mvc.observer to javafx.fxml;
}