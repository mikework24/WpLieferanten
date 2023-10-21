package de.mfroese.wplieferanten.logic;

import de.mfroese.wplieferanten.logic.database.DbManager;
import de.mfroese.wplieferanten.model.Supplier;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton der Lieferanten,
 * zum bereitstellen der liste im ganzen Programm.
 */
public class SupplierHolder {
    //region Konstanten
    //endregion

    //region Attribute
    private static SupplierHolder instance;

    private final List<Supplier> suppliersAll;
    private final ObservableList<Supplier> suppliers;
    private final ListChangeListener<Supplier> listener;
    //endregion

    //region Konstruktoren
    private SupplierHolder() {

        suppliersAll = new ArrayList<>();

        suppliers = FXCollections.observableArrayList(suppliersToObserve ->
                new Observable[] {suppliersToObserve.companyProperty(), suppliersToObserve.websiteProperty(),
                        suppliersToObserve.contactProperty(), suppliersToObserve.emailProperty(),
                        suppliersToObserve.phoneProperty(), suppliersToObserve.vatProperty(),
                        suppliersToObserve.profit_marginProperty() });

        //Lieferanten auslesen und in die Liste einfügen
        suppliers.addAll(DbManager.getInstance().readAllSuppliers());
        suppliersAll.addAll(suppliers);

        //Setzt die Listener Eigenschaften
        listener = change -> {
            while (change.next()) {
                if (change.wasReplaced()) System.out.println("Listenelement ersetzt");
                else if (change.wasUpdated()) {
                    //Supplier supplierToUpdate = change.getList().get(change.getFrom());
                    //DbManager.getInstance().update(supplierToUpdate);
                    //System.out.println("change.wasUpdated");
                }
                else if (change.wasRemoved()) {
                    Supplier supplierToDelete = change.getRemoved().get(0);
                    DbManager.getInstance().delete(supplierToDelete);

                    suppliersAll.clear();
                    suppliersAll.addAll(suppliers);

                    //System.out.println("change.wasRemoved");
                }
                else if (change.wasAdded()) {
                    Supplier supplierToInsert = change.getAddedSubList().get(0);
                    DbManager.getInstance().insert(supplierToInsert);

                    suppliersAll.clear();
                    suppliersAll.addAll(suppliers);

                    //System.out.println("change.wasAdded");
                }
            }
        };

        //Starte den Listener
        activateListener();

    }
    //endregion

    //region Methoden
    public static synchronized SupplierHolder getInstance() {
        if (instance == null) instance = new SupplierHolder();
        return instance;
    }

    public ObservableList<Supplier> getSuppliers() {
        deactivateListener();

        suppliers.clear();
        suppliers.addAll(suppliersAll);

        activateListener();

        return suppliers;
    }

    private void activateListener() {
        suppliers.addListener(listener);
    }

    private void deactivateListener() {
        suppliers.removeListener(listener);
    }

    public void update(Supplier supplier) {
        DbManager.getInstance().update(supplier);
    }

    public ObservableList<Supplier> reload() {

        deactivateListener();

        //Lieferanten auslesen und in die Liste einfügen
        suppliers.clear();
        suppliers.addAll(DbManager.getInstance().readAllSuppliers());

        suppliersAll.clear();
        suppliersAll.addAll(suppliers);

        activateListener();

        return suppliers;
    }

    public ObservableList<Supplier> search(String searchString) {
        deactivateListener();

        suppliers.clear();

        if (searchString.length() > 0) {
            // Eine neue Liste für die gefilterten Lieferanten erstellen
            List<Supplier> filteredList = new ArrayList<>();

            // Filtern der Lieferanten nach dem Suchbegriff
            for (Supplier supplier : suppliersAll) {
                String searchTarget = supplier.search();
                if (searchTarget != null && searchTarget.toLowerCase().contains(searchString.toLowerCase())) {
                    filteredList.add(supplier);
                }
            }

            suppliers.addAll(filteredList);
        } else {
            suppliers.addAll(suppliersAll);
        }

        activateListener();

        return suppliers;
    }

    //endregion
}