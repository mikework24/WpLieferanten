package de.mfroese.wplieferanten.logic.database;

import de.mfroese.wplieferanten.gui.MainController;
import de.mfroese.wplieferanten.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import java.sql.*;

/**
 * DaoKlasse für Produkt-Objekte
 * Kann Produkte auslesen, speichern, ändern un löschen.
 * Kennt die Tabellenstruktur der Produkte aus der Datenbank.
 */
public class DaoProduct implements Dao<Product> {
    //region Konstanten
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_POST_TITLE = "post_title";
    public static final String COLUMN_GUID = "guid";
    public static final String COLUMN_POST_PARENT = "post_parent";
    public static final String COLUMN_POST_ID = "post_id";
    public static final String COLUMN_META_ID = "meta_id";
    public static final String COLUMN_META_KEY = "meta_key";
    public static final String COLUMN_META_VALUE = "meta_value";

    //wp_posts
    public static final String SQL_SELECT_ALL_PRODUCTS = "SELECT * FROM wp_posts WHERE post_type = 'product_variation' AND post_status = 'publish' OR post_type = 'product' AND post_status = 'publish'";
    public static final String SQL_UPDATE_PRODUCT_BY_ID = "UPDATE wp_posts SET post_title = ? WHERE wp_posts.ID = ?;";
    public static final String SQL_DELETE_PRODUCT_BY_ID = "UPDATE wp_posts SET post_status='trash' WHERE ID = ?";
    public static final String SQL_SELECT_ALL_PRODUCTS_POSTMETA = "SELECT * FROM wp_postmeta WHERE ";


    //endregion

    //region Attribute
    int rows = 0;
    public StringBuilder sqlSelectAllProductsPostmeta = new StringBuilder();
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    @Override
    public void insert(Connection connection, Product product) {}

    @Override
    public List<Product> readAll(Connection connection) {
        //Leere Liste generieren, in der die Produkte aus der Datenbank gespeichert werden sollen
        List<Product> products = new ArrayList<>();

        //Statement-Objekt deklarieren
        Statement statement = null;

        //erste Anfrage ließt aus dem wp_posts
        try {

            MainController.updateProgressBar(0.10);

            //Statement-Objekt über die Verbindung erzeugen
            statement = connection.createStatement();

            //Sql-Abfrage über das Statement-Objekt ausführen und Ergebnismenge in Variable speichern
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_PRODUCTS);

            sqlSelectAllProductsPostmeta.append(SQL_SELECT_ALL_PRODUCTS_POSTMETA);

            //Ergebnismenge iterieren und für jede Zeile ein neues Produkt mit den sich darin befindenden Infos erzeugen
            while (resultSet.next()) {

                //Erzeuge einen Query fuer die 2 Anfrage.
                if(rows != 0) sqlSelectAllProductsPostmeta.append("OR ");
                sqlSelectAllProductsPostmeta.append("post_id = '").append(resultSet.getInt(COLUMN_ID)).append("' ");
                rows++;

                int id                 = resultSet.getInt(COLUMN_ID);
                String title           = resultSet.getString(COLUMN_POST_TITLE);
                double price           = 0.0;
                double regularPrice    = 0.0;
                double mwSt            = 0.0;
                String fixedPriceRules = "";
                String stockStatus     = "";
                String wpLink          = resultSet.getString(COLUMN_GUID);
                int parentId           = resultSet.getInt(COLUMN_POST_PARENT);
                int supplierId         = 0;
                String supplierUrl     = "";

                Product product = new Product(id, title, price, regularPrice, mwSt,
                        fixedPriceRules, stockStatus, wpLink, parentId, supplierId, supplierUrl);

                //Produkt in die Liste einfuegen
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Statement und Verbindung schließen
            try {
                if (statement != null) statement.close();
                //connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MainController.updateProgressBar(0.10);

        //zweite Anfrage ließt aus dem wp_postmeta
        try {

            //Statement-Objekt über die Verbindung erzeugen
            statement = connection.createStatement();

            //Sql-Abfrage über das Statement-Objekt ausführen und Ergebnismenge in Variable speichern
            sqlSelectAllProductsPostmeta.append(";");
            ResultSet resultSet = statement.executeQuery(String.valueOf(sqlSelectAllProductsPostmeta));
            sqlSelectAllProductsPostmeta.setLength(0);
            rows = 0;

            //Ergebnismenge iterieren vervollstaendigen von vorhanenen Produkt mit den sich darin befindenden Infos
            while (resultSet.next()) {
                String postId = resultSet.getString(COLUMN_POST_ID);
                String metaKey = resultSet.getString(COLUMN_META_KEY);

                Set<String> searchedKeys = new HashSet<>(Arrays.asList("_price", "_regular_price", "_fixed_price_rules",
                        "_stock_status", "supplier_id", "supplier_url"));

                //Bei einem gesuchen metaKey wird weitergemacht
                if (searchedKeys.contains(metaKey)) {

                    // Werte in der Liste speichern
                    for (Product product : products) {
                        if(product.getId() == Integer.parseInt(postId)){

                            switch (metaKey) {
                                case "_price"             -> product.setPrice(resultSet.getDouble("meta_value"));
                                case "_regular_price"     -> product.setRegularPrice(resultSet.getDouble("meta_value"));
                                case "_fixed_price_rules" -> product.setPriceRules(resultSet.getString("meta_value"));
                                case "_stock_status"      -> product.setStock(resultSet.getString("meta_value"));
                                case "supplier_id"        -> product.setSupplierId(resultSet.getInt("meta_value"));
                                case "supplier_url"       -> product.setSupplierUrl(resultSet.getString("meta_value"));
                                default -> {}
                            }
                        }
                    }
                }

                MainController.updateProgressBar((0.70/rows));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Statement und Verbindung schließen
            try {
                if (statement != null) statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Liste von Produkten aus der Datenbank zurückgeben
        return products;
    }

    @Override
    public void update(Connection connection, Product product) {

        // todo zum updaten am besten herausfinden was verändert wurde. um mehrere anfragen zu vermeiden
        // todo oder zusammenfassen das in einem update mehrere metadaten geändert werden

        //System.out.println(SQL_UPDATE_PRODUCT_BY_ID);

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(SQL_UPDATE_PRODUCT_BY_ID);
            statement.setString(1, product.getTitle());
            statement.setInt(2, product.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Produkt metadaten speichern
        updatePostMeta(connection, product.getId(), "_price", String.valueOf(product.getPrice()));
        updatePostMeta(connection, product.getId(), "_regular_price", String.valueOf(product.getPrice()));
        updatePostMeta(connection, product.getId(), "_fixed_price_rules", product.getPriceRules());
        updatePostMeta(connection, product.getId(), "_stock_status", product.getStock());
        updatePostMeta(connection, product.getId(), "supplier_id", String.valueOf(product.getSupplierId()));
        updatePostMeta(connection, product.getId(), "supplier_url", product.getSupplierUrl());

        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Connection connection, Product product) {

        //System.out.println(SQL_DELETE_PRODUCT_BY_ID);

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_DELETE_PRODUCT_BY_ID);
            statement.setInt(1, product.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //helpers
    public static void updatePostMeta(Connection connection, int postId, String metaKey, String metaValue) {

        //System.out.println("SELECT meta_id, meta_value FROM wp_postmeta WHERE post_id = ? AND meta_key = ?");

        PreparedStatement statement = null;

        // Überprüfen, ob der Eintrag bereits existiert
        String query = "SELECT meta_id, meta_value FROM wp_postmeta WHERE post_id = ? AND meta_key = ?";
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, postId);
            statement.setString(2, metaKey);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Eintrag existiert -> Update durchführen
                    int metaId = resultSet.getInt(COLUMN_META_ID);
                    String metaValueDb = resultSet.getString(COLUMN_META_VALUE);

                    if (!metaValueDb.equals(metaValue)) {
                        updatePostMetaValue(connection, metaId, metaValue);
                    }
                } else {
                    // Eintrag existiert nicht -> Insert durchführen
                    insertPostMetaValue(connection, postId, metaKey, metaValue);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updatePostMetaValue(Connection connection, int metaId, String metaValue) throws SQLException {
        String query = "UPDATE wp_postmeta SET meta_value = ? WHERE meta_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, metaValue);
            preparedStatement.setInt(2, metaId);
            preparedStatement.executeUpdate();
        }
    }

    private static void insertPostMetaValue(Connection connection, int postId, String metaKey, String metaValue) throws SQLException {
        String query = "INSERT INTO wp_postmeta (post_id, meta_key, meta_value) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, postId);
            preparedStatement.setString(2, metaKey);
            preparedStatement.setString(3, metaValue);
            preparedStatement.executeUpdate();
        }
    }


    //endregion
}
