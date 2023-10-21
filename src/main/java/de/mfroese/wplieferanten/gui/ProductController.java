package de.mfroese.wplieferanten.gui;

import de.mfroese.wplieferanten.logic.ProductHolder;
import de.mfroese.wplieferanten.logic.Helpers;
import de.mfroese.wplieferanten.model.ComboBoxItem;
import de.mfroese.wplieferanten.model.Product;
import de.mfroese.wplieferanten.model.Supplier;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductController {
    @FXML
    public StackPane rootPane;
    //region Konstanten
    //endregion

    //region Attribute
    @FXML
    private TextArea title;
    @FXML
    private TextField price;
    @FXML
    private ComboBox priceRules;
    @FXML
    private ComboBox stock;
    @FXML
    private ComboBox supplierId;
    @FXML
    private TextArea supplierUrl;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private Product selectedProduct;


    //Controller verbinden
    private MainController mainController;

    // Methode zum Setzen des MainControllers
    public void mainController(MainController mainController) {
        this.mainController = mainController;
    }
    //endregion

    //region Konstruktoren
    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            selectedProduct = mainController.productTableView.getSelectionModel().getSelectedItem().getValue();

            //fuelle die felder aus
            if (selectedProduct != null) {
                title.setText(selectedProduct.getTitle());
                price.setText(String.valueOf(selectedProduct.getPrice()));

                // Staffelpreis (ComboBox) mit Optionen füllen
                List<String> priceRulesList = Helpers.priceRulesToReadebelList(selectedProduct.getPriceRules());
                priceRules.getItems().setAll(priceRulesList);

                int maxVisibleRows = 10; // Maximale Anzahl der sichtbaren Zeilen
                priceRules.setVisibleRowCount(Math.min(priceRulesList.size(), maxVisibleRows));
                priceRules.setDisable(priceRulesList.isEmpty());
                if (!priceRulesList.isEmpty()) {
                    priceRules.setValue(priceRulesList.get(0));
                }


                // Stock (ComboBox) mit Optionen füllen
                List<ComboBoxItem> stockOptions = Arrays.asList(
                        new ComboBoxItem("instock", "Verfügbar"),
                        new ComboBoxItem("outofstock", "Nicht verfügbar"),
                        new ComboBoxItem("onbackorder", "Lieferrückstau")
                );
                stock.getItems().setAll(stockOptions);
                stock.setValue(Helpers.getComboBoxItemByValue(stockOptions, selectedProduct.getStock()));


                // Lieferanten (ComboBox) mit Optionen füllen
                List<ComboBoxItem> supplierOptions = new ArrayList<>();
                supplierOptions.add( new ComboBoxItem("0" , "Lieferant Wählen"));
                for (Supplier supplier : mainController.suppliers) {
                    supplierOptions.add( new ComboBoxItem(String.valueOf(supplier.getId()) , supplier.getCompany()) );
                }
                supplierId.getItems().setAll(supplierOptions);
                supplierId.setValue(Helpers.getComboBoxItemByValue(supplierOptions, String.valueOf(selectedProduct.getSupplierId())));
                supplierId.setVisibleRowCount(supplierOptions.size());


                // todo 2.0 hinweiß wenn die url nicht vom lieferanten ist (auch in der tableview rosa markieren)

                //Die Url wird auf eine eingabe vorbereitet
                supplierId.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.toString().equals("Lieferant Wählen")) {
                        //supplierUrl.setText("");
                        supplierUrl.setDisable(true);
                    }else{
                        supplierUrl.setDisable(false);
                        for (Supplier supplier : mainController.suppliers) {
                            if(newValue.toString().equals(supplier.getCompany())){

                                String website = supplier.getWebsite();
                                if (website.endsWith("/")) {
                                    website = website.substring(0, website.length() - 1);
                                }

                                if(supplierUrl.getText().length() == 0){
                                    supplierUrl.setText(website + "/produkt_seite");
                                }
                            }
                        }
                    }
                });

                supplierUrl.setText(selectedProduct.getSupplierUrl());
            }


            //Im Preisfeld darf nur ein gueltiger wert sein
            price.textProperty().addListener((observable, oldValue, newValue) -> {
                String enterdValue = newValue.replaceAll("[^0-9.]", "");
                String filteredValue = enterdValue.replace(",", ".");
                if (filteredValue.matches("^\\d*\\.?\\d*$")) {
                    price.setText(enterdValue);
                } else {
                    price.setText(oldValue);
                }
            });

            //Im Titel darf nicht leer sein
            title.textProperty().addListener((observable, oldValue, newValue) -> {
                saveButton.setDisable(title.getText().isBlank());
            });

        });

    }
    //endregion

    //region Methoden
    @FXML
    protected void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }


    @FXML
    protected void changeProductThread() {

        //Threads werden geladen um daten aus dem Internet nachzuladen.
        Platform.runLater(() -> {
            // Starten Sie den Datenladevorgang in separaten Threads
            Thread saveProductThread = new Thread(this::changeProductAndCloseWindow);

            // Setzen Sie die Threads als Hintergrundthreads, damit die Oberfläche während des Ladens der Daten nutzbar bleibt
            saveProductThread.setDaemon(true);

            // Starten Sie die Threads
            saveProductThread.start();
        });
    }


    @FXML
    protected void changeProductAndCloseWindow() {

        setOverlayVisible(true);

        // Daten im Objekt ablegen
        selectedProduct.setTitle(title.getText());

        double priceDouble;
        try {
            priceDouble = Double.parseDouble(price.getText());
        } catch (NumberFormatException e) {
            priceDouble = 0.0;
        }
        selectedProduct.setPrice(priceDouble);

        if(stock.getValue() != null){
            String selectedStockValue = ((ComboBoxItem) stock.getValue()).getValue();
            selectedProduct.setStock(selectedStockValue);
        }

        if(supplierId.getValue() != null){
            String selectedSupplierId  = ((ComboBoxItem) supplierId.getValue()).getValue();
            selectedProduct.setSupplierId(Integer.parseInt(selectedSupplierId));
        }

        selectedProduct.setSupplierUrl(supplierUrl.getText());

        // todo 3.0 Produkt Preis und Reduzierter Preis, 2 eingabefeld erstellen

        // Updatet vorhandenes Produkt in der Datenbank
        ProductHolder.getInstance().update(selectedProduct);

        // Produktänderungen in der Produktliste aktualisieren
        if (selectedProduct != null && mainController.productList != null) {

            //Produkte
            int pIndex = mainController.productList.indexOf(selectedProduct);
            if (pIndex != -1) mainController.productList.set(pIndex, selectedProduct);

            //Produkt Variationen
            int pvIndex = mainController.productVariations.indexOf(selectedProduct);
            if (pvIndex != -1) mainController.productVariations.set(pvIndex, selectedProduct);

            // Aktualisiere die supplierTableView
            mainController.productTableView.refresh();
        }

        // Fenster schließen
        closeWindow();


        setOverlayVisible(false);
    }

    // Methode zum Aktivieren/Deaktivieren des Overlays
    private void setOverlayVisible(boolean visible) {
        rootPane.setVisible(visible);
        rootPane.setDisable(!visible);

        title.setDisable(visible);
        price.setDisable(visible);
        priceRules.setDisable(visible);
        stock.setDisable(visible);
        supplierId.setDisable(visible);
        supplierUrl.setDisable(visible);
        cancelButton.setDisable(visible);
        saveButton.setDisable(visible);
    }

    //endregion
}