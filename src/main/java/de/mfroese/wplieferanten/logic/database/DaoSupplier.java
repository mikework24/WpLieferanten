package de.mfroese.wplieferanten.logic.database;

import de.mfroese.wplieferanten.gui.MainController;
import de.mfroese.wplieferanten.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DaoKlasse für Lieferanten-Objekte
 * Kann Lieferanten auslesen, speichern, ändern un löschen.
 * Kennt die Tabellenstruktur der Lieferanten aus der Datenbank.
 */
public class DaoSupplier implements Dao<Supplier> {
    //region Konstanten

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_VAT = "vat";
    public static final String COLUMN_PROFIT_MARGIN = "profit_margin";
    public static final String SQL_SELECT_ALL_SUPPLIERS = "SELECT * FROM wp_suppliers";
    public static final String SQL_INSERT_SUPPLIER = "INSERT INTO wp_suppliers (company, website, contact, email, phone, vat, profit_margin) VALUES (?,?,?,?,?,?,?)";
    public static final String SQL_UPDATE_SUPPLIER_BY_ID = "UPDATE wp_suppliers SET company=?, website=?, contact=?, email=?, phone=?, vat=?, profit_margin=? WHERE id=?";
    public static final String SQL_DELETE_SUPPLIER_BY_ID = "DELETE FROM wp_suppliers WHERE id=?";

    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    @Override
    public void insert(Connection connection, Supplier supplier) {

        //Statement-Objekt deklarieren
        PreparedStatement statement = null;
        try {
            //Statement-Objekt über die Verbindung erzeugen
            statement = connection.prepareStatement(SQL_INSERT_SUPPLIER, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, supplier.getCompany());
            statement.setString(2, supplier.getWebsite());
            statement.setString(3, supplier.getContact());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getPhone());
            statement.setDouble(6, supplier.getVat());
            statement.setDouble(7, supplier.getProfit_margin());

            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();

            int insertId;

            if (resultSet.next()) {
                insertId = resultSet.getInt(1);
                supplier.setId(insertId);
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
    }

    @Override
    public List<Supplier> readAll(Connection connection) {
        //Leere Liste generieren, in der die Lieferanten aus der Datenbank gespeichert werden sollen
        List<Supplier> suppliers = new ArrayList<>();

        //Statement-Objekt deklarieren
        Statement statement = null;
        try {

            //Statement-Objekt über die Verbindung erzeugen
            statement = connection.createStatement();

            //Sql-Abfrage über das Statement-Objekt ausführen und Ergebnismenge in Variable speichern
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_SUPPLIERS);

            //Ergebnismenge iterieren und für jede Zeile ein neuer Lieferant mit den sich darin befindenden Infos erzeugen
            while (resultSet.next()) {

                Supplier supplier = new Supplier(
                    resultSet.getInt(COLUMN_ID),
                    resultSet.getString(COLUMN_COMPANY),
                    resultSet.getString(COLUMN_WEBSITE),
                    resultSet.getString(COLUMN_CONTACT),
                    resultSet.getString(COLUMN_EMAIL),
                    resultSet.getString(COLUMN_PHONE),
                    resultSet.getDouble(COLUMN_VAT),
                    resultSet.getDouble(COLUMN_PROFIT_MARGIN)
                );

                //Lieferant in die Liste einfügen
                suppliers.add(supplier);

                //Fortschrittbalken
                MainController.updateProgressBar(0.4);
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

        //Liste von Tieren aus der Datenbank zurückgeben
        return suppliers;
    }

    @Override
    public void update(Connection connection, Supplier supplier) {

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_UPDATE_SUPPLIER_BY_ID);
            statement.setString(1, supplier.getCompany());
            statement.setString(2, supplier.getWebsite());
            statement.setString(3, supplier.getContact());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getPhone());
            statement.setDouble(6, supplier.getVat());
            statement.setDouble(7, supplier.getProfit_margin());
            statement.setInt(8, supplier.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void delete(Connection connection, Supplier supplier) {

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_DELETE_SUPPLIER_BY_ID);
            statement.setInt(1, supplier.getId());

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //endregion
}
