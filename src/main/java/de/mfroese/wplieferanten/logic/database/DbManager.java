package de.mfroese.wplieferanten.logic.database;

import de.mfroese.wplieferanten.logic.StorageManager;
import de.mfroese.wplieferanten.model.Product;
import de.mfroese.wplieferanten.model.Supplier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * Baut Verbindung zu Datenbanken auf und hat Zugriff
 * auf die jeweiligen DAO-Objekte aller Modellklassen
 * des Projektes.
 */
public class DbManager {
    //region Konstanten
    private static final String CONNECTION_API_PREFIX = "jdbc:mysql://";
    //endregion

    //region Attribute
    private static DbManager instance;
    private final DaoSupplier daoSupplier;
    private final DaoProduct daoProduct;
    //endregion

    //region Konstruktoren
    private DbManager() {
        daoSupplier = new DaoSupplier();
        daoProduct = new DaoProduct();
    }
    //endregion

    //region Methoden
    public static synchronized DbManager getInstance() {
        if (instance == null) instance = new DbManager();
        return instance;
    }

    private Connection getConnection() {
        Connection connection = null;

        //Aufbau der Verbindung zwischen Programm und Datenbank
        try {
            //Daten aus dem storageManager für die Verbindung laden
            //StorageManager storageManager = StorageManager.getInstance();
            //String serverIp       = storageManager.getServerIp();
            //String dbName         = storageManager.getServerDatabase();
            //String dbUserName     = storageManager.getServerUser();
            //String dbUserPassword = storageManager.getServerPassword();

            String serverIp       = "194.5.156.196";
            String dbName         = "u337745873_wp_shop_demo";
            String dbUserName     = "u337745873_wp_shop_demo";
            String dbUserPassword = "JUtB5yhy9qm3QSe";

            String connectionUrl = CONNECTION_API_PREFIX + serverIp + "/" + dbName;

            connection = DriverManager.getConnection(connectionUrl, dbUserName, dbUserPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public void insert(Object object) {
        if (object instanceof Supplier supplier) daoSupplier.insert(getConnection(), supplier);
        else if (object instanceof Product product) daoProduct.insert(getConnection(), product);
        else System.out.println("Objekt kann nicht eingefügt werden!");
    }

    public List<Supplier> readAllSuppliers() {
        return daoSupplier.readAll(getConnection());
    }
    public List<Product> readAllProducts() {
        return daoProduct.readAll(getConnection());
    }


    public void update(Object object) {
        if (object instanceof Supplier supplier) daoSupplier.update(getConnection(), supplier);
        else if (object instanceof Product product) daoProduct.update(getConnection(), product);
        else System.out.println("Objekt kann nicht aktualisiert werden!");
    }

    public void delete(Object object) {
        if (object instanceof Supplier supplier) daoSupplier.delete(getConnection(), supplier);
        else if (object instanceof Product product) daoProduct.delete(getConnection(), product);
        else System.out.println("Objekt kann nicht gelöscht werden!");
    }

    //endregion
}
