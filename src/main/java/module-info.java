module team.proiectextins {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens team.proiectextins to javafx.fxml;
    opens team.proiectextins.controller to javafx.fxml;
    opens team.proiectextins.domain to javafx.base;
    exports team.proiectextins;
    exports team.proiectextins.controller;
    exports team.proiectextins.domain;
    exports team.proiectextins.service;
    exports team.proiectextins.utils.events;
    exports team.proiectextins.utils.observer;
    exports team.proiectextins.repository;
}