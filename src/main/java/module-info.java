module com.engine.canada {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.java;
    requires javafx.base;

    opens com.engine.canada to javafx.controls,javafx.graphics,javafx.fxml,javafx.base;
    exports com.engine.canada;
}