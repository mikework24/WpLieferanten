module de.mfroese.wplieferanten {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;


    opens de.mfroese.wplieferanten to javafx.fxml;
    exports de.mfroese.wplieferanten;
    exports de.mfroese.wplieferanten.gui;
    opens de.mfroese.wplieferanten.gui to javafx.fxml;
}