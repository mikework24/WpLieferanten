package de.mfroese.wplieferanten.logic.database;

import java.sql.Connection;
import java.util.List;

/**
 * DAO - Data Access Object - Objekt, welches Zugriff auf bestimmte Daten hat.
 * Stellt die CRUD-Operation bereit
 *
 * @param <T> : Typ des Interfaces / Modellklasse, für welches das Interface implementiert wird
 *           TODO 1.0 Dao-Interface implementieren
 */
public interface Dao<T> {

    /**
     * Methode zum Einfügen eines Objektes in die Datenbank
     *
     * @param connection : {@link Connection} : Verbindung zur Datenbank
     * @param object : T : Objekt einer bestimmten Klasse
     */
    void insert(Connection connection, T object);

    /**
     * Methode zum Auslesen von Objekten aus der Datenbank
     *
     * @param connection : {@link Connection} : Verbindung zur Datenbank
     */
    List<T> readAll(Connection connection);

    /**
     * Methode zum Aktualisieren eines Objektes in der Datenbank
     *
     * @param connection : {@link Connection} : Verbindung zur Datenbank
     * @param object : T : Objekt einer bestimmten Klasse
     */
    void update(Connection connection, T object);

    /**
     * Methode zum Löschen eines Objektes aus der Datenbank
     *
     * @param connection : {@link Connection} : Verbindung zur Datenbank
     * @param object : T : Objekt einer bestimmten Klasse
     */
    void delete(Connection connection, T object);
}

