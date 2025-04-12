module FoundationCode {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
	requires org.junit.jupiter.api;

    opens application to javafx.graphics, javafx.fxml, javafx.base;
}
