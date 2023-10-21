package de.mfroese.wplieferanten.logic;

import java.sql.*;

public class MyDatabase {
    //region Konstanten

    //endregion


    //region Konstruktoren

    //endregion

    //region Methoden
    public static boolean connectionDataValid(String getIp, String getDb, String getUser, String getPassword) {
        boolean response = false;

        Connection connection = null;

        try {
            // Verbindung zur Datenbank herstellen
            connection = DriverManager.getConnection("jdbc:mysql://" + getIp + "/" + getDb, getUser, getPassword);

            // Überprüfen, ob die Verbindung erfolgreich hergestellt wurde
            if (connection != null && !connection.isClosed()) {
                response = true;
            }

        } catch (SQLException e) {
            System.out.println("connectionDataValid() Fehler oder Zugangsdaten stimmen nicht!");
            e.printStackTrace();
        } finally {
            try{
                // Ressourcen schließen
                if(connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return response;
    }

    //endregion
}
