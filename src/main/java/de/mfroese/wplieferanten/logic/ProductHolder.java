package de.mfroese.wplieferanten.logic;

import de.mfroese.wplieferanten.logic.database.DbManager;
import de.mfroese.wplieferanten.model.Product;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton der Produkte,
 * zum bereitstellen der liste im ganzen Programm.
 */
public class ProductHolder {
    //region Konstanten
    //endregion

    //region Attribute
    private static ProductHolder instance;

    //enthällt alle produkte wenn die aus der datenbank geladen werden
    private final List<Product> productsAll;
    private final ObservableList<Product> products;
    private final ObservableList<Product> variations;
    private final ListChangeListener<Product> listener;

    //endregion

    //region Konstruktoren
    private ProductHolder() {

        productsAll = new ArrayList<>();

        products = FXCollections.observableArrayList(productsToObserve ->
                new Observable[] {productsToObserve.titleProperty(), productsToObserve.priceProperty(),
                        productsToObserve.regularPriceProperty(), productsToObserve.mwStProperty(),
                        productsToObserve.priceRulesProperty(), productsToObserve.stockProperty(),
                        productsToObserve.wpLinkProperty(), productsToObserve.parentIdProperty(),
                        productsToObserve.supplierIdProperty(), productsToObserve.supplierUrlProperty(),
                        productsToObserve.supplierPriceProperty(), productsToObserve.supplierPriceRulesProperty(),
                        productsToObserve.supplierStockProperty()
                });

        variations = FXCollections.observableArrayList(productsToObserve ->
                new Observable[] {productsToObserve.titleProperty(), productsToObserve.priceProperty(),
                        productsToObserve.regularPriceProperty(), productsToObserve.mwStProperty(),
                        productsToObserve.priceRulesProperty(), productsToObserve.stockProperty(),
                        productsToObserve.wpLinkProperty(), productsToObserve.parentIdProperty(),
                        productsToObserve.supplierIdProperty(), productsToObserve.supplierUrlProperty(),
                        productsToObserve.supplierPriceProperty(), productsToObserve.supplierPriceRulesProperty(),
                        productsToObserve.supplierStockProperty()
                });

        // Alle produkte ungefilter in dieser Liste verwahren, bei änderungen werden diese auch auf diese liste angewendet.
        productsAll.addAll(DbManager.getInstance().readAllProducts());

        // Filtern und Laden der Produkte und Variationen
        loadProducts();
        loadVariations();

        // Listener für Änderungen hinzufügen
        listener = change -> {
            while (change.next()) {

                //System.out.println("ProductHolder -> Listener");
                //System.out.println(change);

                if (change.wasReplaced()) {
                    //System.out.println("Listenelement ersetzt");
                }
                else if (change.wasUpdated()) {
                    //System.out.println("Update");
                }
                else if (change.wasAdded()) {
                    //System.out.println("change.wasAdded()");

                    // Neue Produkte oder Variationen hinzugefügt
                    for (Product product : change.getAddedSubList()) {

                        if (!productsAll.contains(product)) productsAll.add(product);
                        DbManager.getInstance().insert(product);

                        if (product.getParentId() == 0) {
                            // Produkt hinzugefügt
                            if (!products.contains(product)) products.add(product);
                        } else {
                            // Variation hinzugefügt
                            if (!variations.contains(product)) variations.add(product);
                        }
                    }
                } else if (change.wasRemoved()) {
                    //System.out.println("change.wasRemoved()");

                    // Produkte oder Variationen entfernt
                    for (Product product : change.getRemoved()) {

                        productsAll.remove(product);
                        DbManager.getInstance().delete(product);

                        if (product.getParentId() == 0) {
                            // Produkt entfernt
                            products.remove(product);
                        } else {
                            // Variation entfernt
                            variations.remove(product);
                        }
                    }
                }
            }
        };

        //Starte den Listener
        products.addListener(listener);
        variations.addListener(listener);
    }
    //endregion

    //region Methoden
    public static synchronized ProductHolder getInstance() {
        if (instance == null) instance = new ProductHolder();
        return instance;
    }

    private void loadProducts() {
        if(listener != null ) products.removeListener(listener);

        products.clear();
        for (Product product : productsAll) {
            if (product.getParentId() == 0) {
                // Produkt mit parentId = 0 ist ein Produkt
                products.add(product);
            }
        }

        if(listener != null ) products.addListener(listener);
    }

    private void loadVariations() {
        if(listener != null ) variations.removeListener(listener);

        variations.clear();
        for (Product product : productsAll) {
            if (product.getParentId() != 0) {
                // Produkt mit parentId != 0 ist ein Variation
                variations.add(product);
            }
        }

        if(listener != null ) variations.addListener(listener);
    }

    public ObservableList<Product> getProducts() {
        loadProducts();
        return products;
    }

    public ObservableList<Product> getVariations() {
        loadVariations();
        return variations;
    }


    public void update(Product product) {
        DbManager.getInstance().update(product);
    }

    /**
     *  reload sorgt für ein Neuladen von der Datenbank<br>
     *  nach dem reload() kann die:<br>
     *   - getProducts()<br>
     *   - getVariations()<br>
     *  Aufgerufen werden um die neu geladenen listen zu erhalten.
     */
    public void reload() {

        //Produktdaten auslesen und in die Liste einfügen
        productsAll.clear();
        productsAll.addAll(DbManager.getInstance().readAllProducts());

        loadProducts();
        loadVariations();
    }


    //endregion
}