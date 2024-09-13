module org.example.clientapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.example.clientapp to javafx.fxml;
    exports org.example.clientapp;
}