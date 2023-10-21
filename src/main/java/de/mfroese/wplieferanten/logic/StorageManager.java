package de.mfroese.wplieferanten.logic;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class StorageManager {

    private static final String DESTINATION_FOLDER = "data";
    private static final String RESOURCE_FILE = "app.settings";
    private static final String DESTINATION_FILE = DESTINATION_FOLDER + File.separator + RESOURCE_FILE;

    private static StorageManager instance;
    private final Properties properties;

    private StorageManager() {

        // Erstelle das Zielverzeichnis, falls es nicht existiert
        File destinationFolder = new File(DESTINATION_FOLDER);
        if (!destinationFolder.exists()) {
            if(!destinationFolder.mkdir()){

                //Fehler beim speichern des Produktes in der datenbank
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lokaler Speicher Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Sie haben in diesem Verzeichnis keine Schreibrechte. " +
                        "Verschieben Sie das Programmverzeichniss bitte in ein Verzeichniss, " +
                        "in dem sie auch die Schreibrechte ohne Admin besitzen.");

                alert.showAndWait();
            }
        }

        properties = loadSettings();
    }

    public static synchronized StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    private Properties loadSettings() {
        Properties properties = new Properties();

        File settingsFile = new File(DESTINATION_FILE);

        try {
            if (settingsFile.exists()) {
                FileInputStream fileIn = new FileInputStream(settingsFile);
                properties.load(fileIn);
            }
        } catch (IOException e) {
            System.out.println("loadSettings() -> Fehler beim laden");
            e.printStackTrace();
        }

        return properties;
    }

    public void saveDB(String serverPassword, String serverDatabase, String serverIp, String serverUser) {
        properties.setProperty("serverIp", serverIp);
        properties.setProperty("serverDatabase", serverDatabase);
        properties.setProperty("serverUser", serverUser);
        properties.setProperty("serverPassword", serverPassword);

        try (FileOutputStream fileOut = new FileOutputStream(DESTINATION_FILE)) {
            properties.store(fileOut, "App Settings");
        } catch (IOException e) {
            System.out.println("saveDB() -> Fehler beim speichern");
            e.printStackTrace();
        }
    }

    public String getServerPassword() {
        return properties.getProperty("serverPassword");
    }

    public String getServerDatabase() {
        return properties.getProperty("serverDatabase");
    }

    public String getServerIp() {
        return properties.getProperty("serverIp");
    }

    public String getServerUser() {
        return properties.getProperty("serverUser");
    }
}
