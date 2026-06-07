module gamezone {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens gamezone.entities to javafx.base;
    opens gamezone.ui       to javafx.fxml;
    opens gamezone          to javafx.graphics;

    exports gamezone;
    exports gamezone.entities;
    exports gamezone.services;
    exports gamezone.repositories;
    exports gamezone.ui;
}
