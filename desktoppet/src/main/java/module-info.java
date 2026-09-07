module com.tang {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.net.http;
    requires jdk.jsobject;
    opens com.tang to javafx.fxml;
    exports com.tang;
}