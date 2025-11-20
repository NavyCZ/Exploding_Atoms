module com.brovdij.exploding_atoms {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.brovdij.exploding_atoms.presentation to javafx.fxml;

    exports com.brovdij.exploding_atoms.presentation;
    exports com.brovdij.exploding_atoms.logic;
    exports com.brovdij.exploding_atoms.data;
}
