package de.mfroese.wplieferanten.gui;

import de.mfroese.wplieferanten.gui.listview.*;
import de.mfroese.wplieferanten.gui.treelistview.*;
import de.mfroese.wplieferanten.logic.*;
import de.mfroese.wplieferanten.main;
import de.mfroese.wplieferanten.model.Product;
import de.mfroese.wplieferanten.model.Supplier;
import de.mfroese.wplieferanten.settings.AppTexts;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController {

    //region 0. FXML zuweisungen
    private final StorageManager storageManager = StorageManager.getInstance();

    @FXML
    private TabPane tabPane;
    @FXML
    private Text bottomNote;
    @FXML
    private Text progressBarTitle;
    @FXML
    private ProgressBar loadingProgressBar;


    //Lieferanten
    @FXML
    public Tab tabSupplier;

    @FXML
    private Button reloadSupplierButton;
    @FXML
    private Button supplierDelete;
    @FXML
    private Button supplierEdit;
    @FXML
    private Button supplierNew;
    @FXML
    private TextField searchfieldSuppliers;
    @FXML
    private Button btnCleanSearchfieldSuppliers;


    List<Supplier> suppliers;
    @FXML
    TableView<Supplier> supplierTableView;
    @FXML
    private TableColumn<Supplier, Integer> idColumn;
    @FXML
    private TableColumn<Supplier, String> companyColumn;
    @FXML
    private TableColumn<Supplier, String> websiteColumn;
    @FXML
    private TableColumn<Supplier, Double> vatColumn;
    @FXML
    private TableColumn<Supplier, Double> profitMarginColumn;
    @FXML
    private TableColumn<Supplier, String> contactColumn;
    @FXML
    private TableColumn<Supplier, String> emailColumn;
    @FXML
    private TableColumn<Supplier, String> phoneColumn;


    //Produkte
    @FXML
    private Tab tabProduct;
    @FXML
    List<Product> productList;
    @FXML
    List<Product> productVariations;
    @FXML
    private Button reloadProductsButton;
    @FXML
    private Button productDelete;
    @FXML
    private Button productEdit;
    @FXML
    private Button loadSupplierData;
    @FXML
    private TextField searchfieldProducts;
    @FXML
    private Button btnCleanSearchfieldProducts;


    @FXML
    TreeTableView<Product> productTableView;
    @FXML
    private TreeTableColumn<Product, Integer> productIdColumn;
    @FXML
    private TreeTableColumn<Product, String> productTitleColumn;
    @FXML
    private TreeTableColumn<Product, Double> productPriceColumn;
    @FXML
    private TreeTableColumn<Product, String> productPriceRulesColumn;
    @FXML
    private TreeTableColumn<Product, String> productStockColumn;


    @FXML
    private TreeTableColumn<Product, Integer> supplierProductIdColumn;
    @FXML
    private TreeTableColumn<Product, String> supplierProductUrlColumn;
    @FXML
    private TreeTableColumn<Product, Double> supplierProductPriceColumn;
    @FXML
    private TreeTableColumn<Product, String> supplierProductPriceRulesColumn;
    @FXML
    private TreeTableColumn<Product, String> supplierProductStockColumn;


    //Einstellungen
    @FXML
    private Tab tabSettings;
    @FXML
    private TextField serverIp;
    @FXML
    private TextField serverDatabase;
    @FXML
    private TextField serverUser;
    @FXML
    private PasswordField serverPassword;
    @FXML
    private Button setingsSave;
    //endregion


    List<Product> productFilterd = new ArrayList<>();

    boolean newSupplierOption;
    private static MainController instance;


    @FXML
    public void initialize() {
        instance = this;

        // Die Methoden werden nach dem Initializieren der FXML gestartet
        deactivateSupplierGui();
        deactivateProductGui();

        tabHandler();
        tableSizeHandler();
        supplierHandler();
        productHandler();

        Platform.runLater(this::loadFromDatabaseThread);
    }
    private void loadFromDatabaseThread() {
        //Threads werden geladen um daten aus dem Internet nachzuladen.
        Platform.runLater(() -> {

            // Starten Sie den Datenladevorgang in separaten Threads
            Thread supplierThread = new Thread(this::loadSuppliers);

            // Setzen Sie die Threads als Hintergrundthreads, damit die Oberfläche während des Ladens der Daten nutzbar bleibt
            supplierThread.setDaemon(true);

            // Starten Sie die Threads
            supplierThread.start();
        });
    }
    private void tabHandler() {
        //Tab Lieferant
        tabSupplier.setOnSelectionChanged(event -> {
            if (tabSupplier.isSelected()) {
                supplierTableView.getSelectionModel().clearSelection();
                supplierDelete.setDisable(true);
                supplierEdit.setDisable(true);
            }
        });

        //Tab Produkte
        tabProduct.setOnSelectionChanged(event -> {
            if (tabProduct.isSelected()) {
                productTableView.getSelectionModel().clearSelection();
                productDelete.setDisable(true);
                productEdit.setDisable(true);
            }
        });

        //Tab Einstellungen
        tabSettings.setOnSelectionChanged(event -> {
            if (tabSettings.isSelected()) {
                loadSettings();
            }
        });
    }
    private void tableSizeHandler() {
        //Groese der Zeilen automatisch ändern
        supplierTableView.widthProperty().addListener((observable, oldValue, newValue) -> {
            double freeTableWidth = (newValue.doubleValue() - 792) / 16.5;

            idColumn.setPrefWidth(40 + (freeTableWidth * 2));
            companyColumn.setPrefWidth(145 + (freeTableWidth * 2.41));
            websiteColumn.setPrefWidth(140 + (freeTableWidth * 2.5));

            contactColumn.setPrefWidth(120 + (freeTableWidth * 2.92));
            emailColumn.setPrefWidth(110 + (freeTableWidth * 3.182));
            phoneColumn.setPrefWidth(102 + (freeTableWidth * 3.5));
        });


        productTableView.widthProperty().addListener((observable, oldValue, newValue) -> {
            double freeTableWidth = (newValue.doubleValue() - 810) / 22;

            productIdColumn.setPrefWidth(58 + (freeTableWidth * 1.5));
            productTitleColumn.setPrefWidth(130 + (freeTableWidth * 4.8));
            productPriceColumn.setPrefWidth(70 + (freeTableWidth * 1.2));
            productPriceRulesColumn.setPrefWidth(110 + (freeTableWidth * 2.28));
            productStockColumn.setPrefWidth(60 + (freeTableWidth * 1.67));

            supplierProductIdColumn.setPrefWidth(35 + (freeTableWidth * 1.72));
            supplierProductUrlColumn.setPrefWidth(80 + (freeTableWidth * 4.4));
            supplierProductPriceColumn.setPrefWidth(65 + (freeTableWidth * 1.2));
            supplierProductPriceRulesColumn.setPrefWidth(110 + (freeTableWidth * 2.28));
            supplierProductStockColumn.setPrefWidth(60 + (freeTableWidth * 1.67));
        });
    }


    //region 1. Tab Lieferanten

    private void supplierHandler() {
        supplierTableView.setOnMouseClicked(event -> {
            Supplier selectedSupplier = supplierTableView.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) {
                supplierDelete.setDisable(false);
                supplierEdit.setDisable(false);
            } else {
                supplierDelete.setDisable(true);
                supplierEdit.setDisable(true);
            }
        });

        //Suchfeldes steuern.
        searchfieldSuppliers.textProperty().addListener((observable, oldValue, newValue) -> {

            //Suche instant ausfuehren
            searchSupplier();

            //Loeschen Button des Suchfeldes steuern.
            btnCleanSearchfieldSuppliers.setDisable(newValue.isBlank());
        });
    }

    private void loadSuppliers() {

        supplierTableView.setRowFactory(tv -> {
            TableRow<Supplier> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1 && row.isEmpty()) {
                    supplierTableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });

        // Setze die Wertefabrik für jede Spalte
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        companyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompany()));
        companyColumn.setCellFactory(TableCellTooltip.forTableColumn());
        companyColumn.setCellFactory(TableCellMenu.forTableColumn());

        websiteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWebsite()));
        websiteColumn.setCellFactory(column -> new TableCellHyperlink<>());

        vatColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getVat()).asObject());
        vatColumn.setCellFactory(TableCellPercentage.forTableColumn());

        profitMarginColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProfit_margin()).asObject());
        profitMarginColumn.setCellFactory(TableCellPercentage.forTableColumn());

        contactColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContact()));
        contactColumn.setCellFactory(TableCellMenu.forTableColumn());

        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        emailColumn.setCellFactory(TableCellMenu.forTableColumn());

        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        phoneColumn.setCellFactory(TableCellMenu.forTableColumn());


        //Laden der Lieferanten
        reloadSuppliers();

        //Laden der Produkte
        loadProducts();
    }

    @FXML
    private void reloadSuppliersThread() {
        //Threads werden geladen um daten aus dem Internet nachzuladen.
        Platform.runLater(() -> {
            // Starten Sie den Datenladevorgang in separaten Threads
            Thread supplierThread = new Thread(this::reloadSuppliers);

            // Setzen Sie die Threads als Hintergrundthreads, damit die Oberfläche während des Ladens der Daten nutzbar bleibt
            supplierThread.setDaemon(true);

            // Starten Sie die Threads
            supplierThread.start();
        });
    }

    void reloadSuppliers() {

        //Deaktiviere alle anderen Felder waehrend der aktuallisierung
        deactivateSupplierGui();
        startProgress("Lieferanten");

        // Lieferanten aus der Datenbank neu laden
        supplierTableView.setItems(SupplierHolder.getInstance().reload());

        // Nach dem laden der Lieferanten werden die elemente wieder aktiviert
        activateSupplierGui();
        endProgress();

        supplierTableView.refresh();

        suppliers = SupplierHolder.getInstance().getSuppliers();
    }


    @FXML
    protected void supplierButton(ActionEvent event){

        newSupplierOption = event.getSource().toString().contains("id=supplierNew");

        try {
            FXMLLoader loader = new FXMLLoader(main.class.getResource("supplier-view.fxml"));
            Parent root = loader.load();

            // Zugriff auf den Controller der product-view erhalten
            SupplierController supplierController = loader.getController();

            // Kontroller miteinander verbinden
            supplierController.mainController(this);

            Stage stage = new Stage();
            if(newSupplierOption){
                stage.setTitle("Neuer Lieferant");
                cleanSearchfieldSuppliers();
            }else{
                stage.setTitle("Lieferant bearbeiten");
            }
            stage.setScene(new Scene(root));

            stage.setMinWidth(310);
            stage.setMinHeight(500);
            stage.setWidth(310);
            stage.setHeight(500);
            stage.setResizable(false);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void deleteSupplier(){

        // Überprüfe, ob eine Zeile ausgewählt ist
        if (supplierTableView.getSelectionModel().getSelectedItem() != null) {

            // Hole das ausgewählte Produkt
            Supplier selectedSupplier = supplierTableView.getSelectionModel().getSelectedItem();

            // Erstelle einen Bestätigungsdialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Lieferant löschen");
            alert.setHeaderText("Möchten Sie den Lieferanten '" + selectedSupplier.getCompany() + "' wirklich löschen?");
            //alert.setContentText(selectedSupplier.getCompany());

            // Ändere die Beschriftungen der Buttons
            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("Löschen");

            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelButton.setText("Abbrechen");

            // Warte auf die Bestätigung des Benutzers
            Optional<ButtonType> result = alert.showAndWait();

            // Überprüfe, ob der Benutzer "OK" ausgewählt hat
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Löschen des Lieferanten
                SupplierHolder.getInstance().getSuppliers().remove(selectedSupplier);

                // Aktuallisiert die Tableview wenn eine Suche verwendet wurde
                if(searchfieldSuppliers.getText().length() > 0) searchSupplier();

                // Code zum Aktualisieren der Anzeige, z.B. Tabelle
                supplierTableView.refresh();

                // Auswahl aufheben
                supplierTableView.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    protected void searchSupplier() {
        // Suchbegriff aus dem Suchfeld abrufen
        String searchTerm = searchfieldSuppliers.getText().trim();

        // Durchsucht die Lieferanten aus der Datenbank
        supplierTableView.setItems(SupplierHolder.getInstance().search(searchTerm));
    }

    @FXML
    protected void cleanSearchfieldSuppliers(){
        searchfieldSuppliers.setText("");
        searchSupplier();
    };

    //endregion


    //region 2. Tab Produkte

    private void productHandler() {
        productTableView.setOnMouseClicked(event -> {
            TreeItem<Product> selectedItem = productTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                productDelete.setDisable(false);
                productEdit.setDisable(false);
            } else {
                productDelete.setDisable(true);
                productEdit.setDisable(true);
            }

        });

        //Suchfeld steuern.
        searchfieldProducts.textProperty().addListener((observable, oldValue, newValue) -> {

            //Suche instant ausfuehren
            searchProduct();

            //Loeschen Button des Suchfeldes steuern.
            btnCleanSearchfieldProducts.setDisable(newValue.isBlank());
        });
    }

    private void loadProducts() {

        //todo 4.0 doppelklick öffnet ein vergleichsmenue Produkt vs lieferant

        //Zeilenfarben und Farbe der Variablen Produkte, entfernen der markierung wenn im lerren bereich geklickt wird
        productTableView.setRowFactory(tv -> {
            TreeTableRow<Product> row = new TreeTableRow<>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().remove("alternate-row");
                    getStyleClass().remove("variation-row");

                    //jedes 2 produkt ausgrauen
                    if (!empty && item != null) {
                        int index = getIndex();
                        if (index % 2 == 1) {
                            getStyleClass().add("alternate-row");
                        } else {
                            getStyleClass().remove("alternate-row");
                        }
                    } else {
                        getStyleClass().remove("alternate-row");
                    }

                    // Produkt Variationen anders ausfaerben
                    if (!empty && item != null) {
                        if (item.getParentId() > 0) {
                            getStyleClass().add("variation-row");
                        } else {
                            getStyleClass().remove("variation-row");
                        }
                    } else {
                        getStyleClass().remove("variation-row");
                    }
                }
            };

            // Zeilen markierung aufheben in der TableView wenn ein leerer bereich geklickt wird
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1 && row.isEmpty()) {
                    productTableView.getSelectionModel().clearSelection();
                }
            });

            return row;
        });

        //region Spaltenzuordnung und Zeilenformatierung

        //Produkte
        productIdColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, Integer> param) ->
                        new ReadOnlyObjectWrapper<>(param.getValue().getValue().getId())
        );
        productIdColumn.setCellFactory(column -> new TreeTableCellWpHyperlink<>());


        productTitleColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getTitle())
        );
        productTitleColumn.setCellFactory(TreeTableCellTooltip.forTreeTableColumn());
        productTitleColumn.setCellFactory(TreeTableCellMenu.forTreeTableColumn());


        productPriceColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, Double> param) ->
                        new ReadOnlyObjectWrapper<>(param.getValue().getValue().getPrice())
        );
        productPriceColumn.setCellFactory(TreeTableCellPrice.forTreeTableColumn());


        productPriceRulesColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPriceRules())
        );
        productPriceRulesColumn.setCellFactory(column -> new TreeTableCellPriceRules<>());


        productStockColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getStock())
        );
        productStockColumn.setCellFactory(column -> new TreeTableCellStock<>());


        //Lieferanten
        supplierProductIdColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, Integer> param) ->
                        new ReadOnlyObjectWrapper<>(param.getValue().getValue().getSupplierId())
        );


        supplierProductIdColumn.setCellFactory(column -> new TreeTableCell<Product, Integer>() {
            {
                setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.MIDDLE) && !isEmpty() || event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && !isEmpty()) {

                        // Markiere die Zeile in der supplierTableView basierend auf der ausgewählten Nummer
                        Integer selectedProductId = getItem();
                        if(selectedProductId > 0){
                            // Den Tab "tabSupplier" auswählen
                            tabPane.getSelectionModel().select(tabSupplier);

                            for (Supplier supplier : supplierTableView.getItems()) {
                                if ( selectedProductId.equals(supplier.getId()) ) {
                                    supplierTableView.getSelectionModel().select(supplier);
                                    supplierTableView.scrollTo(supplier);
                                    break;
                                }
                            }
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item == 0) {
                    setText(null);
                    setTooltip(null);
                } else {
                    //dieser tooltip soll den Lieferanten namen geben
                    setTooltip(new Tooltip(item.toString()));

                    setText(String.valueOf(item));
                    getStyleClass().add("cell-text-right");

                    //Tooltip Id -> Company
                    setTooltip(null);
                    for (Supplier supplier : suppliers) {
                        int id = supplier.getId();
                        if (item.equals(id)) {
                            setTooltip(new Tooltip(supplier.getCompany()));
                        }
                    }
                }
            }
        });

        supplierProductUrlColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getSupplierUrl())
        );
        supplierProductUrlColumn.setCellFactory(column -> new TreeTableCellHyperlink<>());


        supplierProductPriceColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, Double> param) ->
                        new ReadOnlyObjectWrapper<>(param.getValue().getValue().getSupplierPrice())
        );
        supplierProductPriceColumn.setCellFactory(TreeTableCellSupplierPrice.forTreeTableColumn());


        supplierProductPriceRulesColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getSupplierPriceRules())
        );
        supplierProductPriceRulesColumn.setCellFactory(column -> new TreeTableCellSupplierPriceRules<>());


        supplierProductStockColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getSupplierStock())
        );
        supplierProductStockColumn.setCellFactory(column -> new TreeTableCellSupplierStock<>());

        //endregion

        //Lade die Produkte aus der Datenbank
        reloadProducts();

    }

    @FXML
    private void reloadProductsThread() {
        //Threads werden geladen um daten aus dem Internet nachzuladen.
        Platform.runLater(() -> {
            // Starten Sie den Datenladevorgang in separaten Threads
            Thread productsThread = new Thread(this::reloadProducts);

            // Setzen Sie die Threads als Hintergrundthreads, damit die Oberfläche während des Ladens der Daten nutzbar bleibt
            productsThread.setDaemon(true);

            // Starten Sie die Threads
            productsThread.start();
        });
    }

    private void reloadProducts() {

        //Deaktiviere alle anderen Felder waehrend der aktuallisierung
        deactivateProductGui();
        searchfieldProducts.setText("");
        startProgress("Produkte");

        //Daten neu laden aus der Datenbank
        ProductHolder.getInstance().reload();

        // Erstelle die Wurzel des Baumtabellenansichts
        TreeItem<Product> rootItem = new TreeItem<>(new Product()); // Platzhalter-Produkt, kann angepasst werden

        //Produktliste laden
        productList = ProductHolder.getInstance().getProducts();

        // Füge die Produkte als Kinder der Wurzel hinzu
        for (Product product : productList) {
            TreeItem<Product> productItem = new TreeItem<>(product);
            rootItem.getChildren().add(productItem);
        }

        // Nach dem laden der Produkte werden die elemente wieder aktiviert
        Platform.runLater(() -> {
            activateProductGui();
            endProgress();
            productTableView.setRoot(rootItem);
            productTableView.setShowRoot(false);
            productTableView.refresh();

            if(!productList.isEmpty()){
                //Button Lieferantendaten Laden aktivieren
                loadSupplierData.setDisable(false);
            }

            //Lade die Variablen Produkte
            reloadProductVariations();
        });

    }

    private void reloadProductVariations() {

        //Deaktiviere alle anderen Felder während der Aktualisierung
        startProgress("Variable Produkte");

        productVariations = ProductHolder.getInstance().getVariations();

        // Füge die Variablenprodukte als Kinder den entsprechenden Produkten hinzu
        for (Product variation : productVariations) {
            int parentId = variation.getParentId();
            Product parentProduct = Helpers.findProductById(parentId, productList);
            if (parentProduct != null) {
                TreeItem<Product> parentItem = Helpers.findTreeItemByValue(productTableView.getRoot(), parentProduct);
                if (parentItem != null) {
                    TreeItem<Product> variationItem = new TreeItem<>(variation);
                    parentItem.getChildren().add(variationItem);
                    variationItem.setExpanded(true);
                    parentItem.setExpanded(true);
                }
            }
        }

        // Nach dem Laden der Variablenprodukte werden die Elemente wieder aktiviert
        Platform.runLater(() -> {
            activateProductGui();
            endProgress();
            productTableView.refresh();

            if (!productList.isEmpty()) {
                //Button Lieferantendaten Laden aktivieren
                loadSupplierData.setDisable(false);
            }
        });
    }

    @FXML
    private void loadProductsFromUrlThread() {
        //Threads werden geladen um daten aus dem Internet nachzuladen.
        Platform.runLater(() -> {
            // Starten Sie den Datenladevorgang in separaten Threads
            Thread supplierProductThread = new Thread(this::loadProductsFromUrl);

            // Setzen Sie die Threads als Hintergrundthreads, damit die Oberfläche während des Ladens der Daten nutzbar bleibt
            supplierProductThread.setDaemon(true);

            // Starten Sie die Threads
            supplierProductThread.start();
        });
    }

    private void loadProductsFromUrl() {

        deactivateProductGui();
        startProgress("Zulieferer Produkte");

        //beredung der zu verarbeitenden Urls
        int totalUrlsP = (int) productList.stream().filter(productUrls -> productUrls.getSupplierUrl() != null && !productUrls.getSupplierUrl().isEmpty()).count();
        int totalUrlsPV = (int) productVariations.stream().filter(productUrls -> productUrls.getSupplierUrl() != null && !productUrls.getSupplierUrl().isEmpty()).count();
        int totalUrls =  totalUrlsP + totalUrlsPV;

        //Tabellen der Produkte fuellen
        for (Product product : productList) {

            // Wenn die productFilterd nicht leer ist und das aktuelle Produkt in der productFilterd vorhanden ist
            if (productFilterd.isEmpty() || productFilterd.contains(product)) {

                String supplierUrl = product.getSupplierUrl();

                if (supplierUrl != null && !supplierUrl.isEmpty()) {

                    Product newProduct = new ProductUrl(product).get();

                    product.setSupplierPrice(newProduct.getSupplierPrice());
                    product.setSupplierPriceRules(newProduct.getSupplierPriceRules());
                    product.setSupplierStock(newProduct.getSupplierStock());

                    // Anzeige aktualisieren
                    Platform.runLater(() -> {
                        // Code zum Aktualisieren der Anzeige, z.B. Tabelle
                        updateProgressBar(0.9 / totalUrls);
                        productTableView.refresh();
                    });
                }
            }
        }

        for (Product product : productVariations) {

            // Wenn die productFilterd nicht leer ist und das aktuelle Produkt in der productFilterd vorhanden ist
            if (productFilterd.isEmpty() || productFilterd.contains(product)) {

                String supplierUrl = product.getSupplierUrl();

                if (supplierUrl != null && !supplierUrl.isEmpty()) {

                    Product newProduct = new ProductUrl(product).get();

                    product.setSupplierPrice(newProduct.getSupplierPrice());
                    product.setSupplierPriceRules(newProduct.getSupplierPriceRules());
                    product.setSupplierStock(newProduct.getSupplierStock());

                    // Anzeige aktualisieren
                    Platform.runLater(() -> {
                        // Code zum Aktualisieren der Anzeige, z.B. Tabelle
                        updateProgressBar(0.9 / totalUrls);
                        productTableView.refresh();
                    });
                }
            }
        }

        //Gui Aktivieren
        activateProductGui();
        if(!productList.isEmpty()){
            //Button Lieferantendaten Laden aktivieren
            loadSupplierData.setDisable(false);
        }
        endProgress();
    }

    @FXML
    protected void editProduct(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(main.class.getResource("product-view.fxml"));
            Parent root = loader.load();

            // Zugriff auf den Controller der product-view erhalten
            ProductController productController = loader.getController();

            // Kontroller miteinander verbinden
            productController.mainController(this);

            Stage stage = new Stage();
            stage.setTitle("Produkt bearbeiten");
            stage.setScene(new Scene(root));

            stage.setMinWidth(310);
            stage.setMinHeight(500);
            stage.setWidth(310);
            stage.setHeight(500);
            stage.setResizable(false);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void deleteProduct(){

        // Überprüfe, ob eine Zeile ausgewählt ist
        if (productTableView.getSelectionModel().getSelectedItem() != null) {

            // Hole das ausgewählte Produkt
            Product selectedProduct = productTableView.getSelectionModel().getSelectedItem().getValue();

            // Erstelle einen Bestätigungsdialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Produkt löschen");
            alert.setHeaderText("Möchten Sie das Produkt wirklich löschen?");
            alert.setContentText(selectedProduct.getTitle());

            // Ändere die Beschriftungen der Buttons
            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("Löschen");

            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelButton.setText("Abbrechen");

            // Warte auf die Bestätigung des Benutzers
            Optional<ButtonType> result = alert.showAndWait();

            // Überprüfe, ob der Benutzer "OK" ausgewählt hat
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Löschen des Produkts
                TreeItem<Product> selectedProductItem = productTableView.getSelectionModel().getSelectedItem();

                // Überprüfe, ob eine Zeile ausgewählt ist
                if (selectedProductItem != null) {

                    // Löschen des Produktes
                    ProductHolder.getInstance().getProducts().remove(selectedProduct);

                    // Aktualisiere das TreeTableView-Modell
                    TreeItem<Product> parentItem = selectedProductItem.getParent();
                    if (parentItem != null) {
                        parentItem.getChildren().remove(selectedProductItem);
                    } else {
                        productTableView.getRoot().getChildren().remove(selectedProductItem);
                    }

                    // Auswahl aufheben
                    productTableView.getSelectionModel().clearSelection();
                }

            }
        }
    }

    @FXML
    protected void searchProduct(){

        // todo suche muß angepasst werden wenn ein child gefunden wird muß das parent auch gelistet werden

        productFilterd.clear();

        // Suchbegriff aus dem Suchfeld abrufen
        String searchTerm = searchfieldProducts.getText().trim();

        // Erstelle die Wurzel des Baumtabellenansichts
        TreeItem<Product> rootItemS = new TreeItem<>(new Product()); // Platzhalter-Produkt, kann angepasst werden
        productTableView.setRoot(rootItemS);
        productTableView.setShowRoot(false);

        // Füge die Produkte als Kinder der Wurzel hinzu
        for (Product product : productList) {
            String productContent = product.search(); // Inhalt aller Spalten als String abrufen

            // Überprüfen, ob der Suchbegriff im Lieferanteninhalt enthalten ist
            if (productContent.toLowerCase().contains(searchTerm.toLowerCase())) {
                TreeItem<Product> productItem = new TreeItem<>(product);
                rootItemS.getChildren().add(productItem);

                productFilterd.add(product);
            }
        }


        // Füge die Variablenprodukte als Kinder den entsprechenden Produkten hinzu
        for (Product variation : productVariations) {
            int parentId = variation.getParentId();
            Product parentProduct = Helpers.findProductById(parentId, productList);

            // Variation
            if (parentProduct != null) {
                TreeItem<Product> parentItem = Helpers.findTreeItemByValue(productTableView.getRoot(), parentProduct);

                // Bei fehlendem Produkt wird Geprueft ob eine Variation in der suche gefunden wird
                if (parentItem == null) {

                    // Überprüfen, ob der Suchbegriff in der Produkt variation enthalten ist
                    String productVariationContent = variation.search();
                    if (productVariationContent.toLowerCase().contains(searchTerm.toLowerCase())) {

                        // Durchlaufe die Produkte
                        for (Product product : productList) {

                            //Suche nach der ParentId
                            if (  product.getId() == parentId ) {

                                //fuege das Produkt hinzu
                                TreeItem<Product> productItem = new TreeItem<>(product);
                                rootItemS.getChildren().add(productItem);

                                productFilterd.add(product);

                                //Finde das grade hinzugefuegte Parent Item fuer die Variation
                                parentItem = Helpers.findTreeItemByValue(productTableView.getRoot(), parentProduct);
                            }
                        }
                    }
                }

                if (parentItem != null) {

                    //Produkt vorhanden -> Variation kann hinzugefuegt werden wenn noetig
                    String productVariationContent = variation.search(); // Inhalt aller Spalten als String abrufen

                    // Überprüfen, ob der Suchbegriff im Lieferanteninhalt enthalten ist
                    if (productVariationContent.toLowerCase().contains(searchTerm.toLowerCase())) {
                        TreeItem<Product> variationItem = new TreeItem<>(variation);
                        parentItem.getChildren().add(variationItem);
                        variationItem.setExpanded(true);
                        parentItem.setExpanded(true);
                    }
                }
            }
        }

        // Nach dem Laden der Variablenprodukte werden die Elemente wieder aktiviert
        Platform.runLater(() -> {
            productTableView.refresh();
        });

    }

    @FXML
    protected void cleanSearchfieldProducts(){
        searchfieldProducts.setText("");
        productFilterd.clear();
        searchProduct();
    };

    //endregion


    //region 3. Tab Einstellungen

    private void loadSettings() {
        if(storageManager.getServerIp() != null){
            serverIp.setText(storageManager.getServerIp());
        }

        if(storageManager.getServerDatabase() != null){
            serverDatabase.setText(storageManager.getServerDatabase());
        }

        if(storageManager.getServerUser() != null){
            serverUser.setText(storageManager.getServerUser());
        }

        if(storageManager.getServerPassword() != null){
            serverPassword.setText(storageManager.getServerPassword());
        }
    }

    @FXML
    protected void onSettingsTest() {
        int minLenght = 2;
        if(
                serverIp.getText().length() >= minLenght &&
                        serverDatabase.getText().length() >= minLenght &&
                        serverUser.getText().length() >= minLenght &&
                        serverPassword.getText().length() >= minLenght
        ){
            bottomNote.setText(AppTexts.CHEQUE_SERVER_CONNECTION);

            if(MyDatabase.connectionDataValid(
                    serverIp.getText(),
                    serverDatabase.getText(),
                    serverUser.getText(),
                    serverPassword.getText())
            ){
                bottomNote.setText(AppTexts.CONNECTION_DATA_VALID);
                setingsSave.setDisable(false);
            }else{
                bottomNote.setText(AppTexts.ERROR_CONNECTION_DATA_NOT_VALID);
            }
        }else{
            bottomNote.setText(AppTexts.ERROR_EMPTY_FEALD);
        }
    }

    @FXML
    protected void onSettingsSave() {
        storageManager.saveDB(serverPassword.getText(), serverDatabase.getText(), serverIp.getText(), serverUser.getText());
        setingsSave.setDisable(true);
        bottomNote.setText(AppTexts.CONNECTION_DATA_SAVED);
    }

    //endregion


    //region 4. Helpers
    private void deactivateSupplierGui() {
        //Deaktiviere alle anderen Felder
        reloadSupplierButton.setDisable(true);
        supplierNew.setDisable(true);
        supplierEdit.setDisable(true);
        supplierDelete.setDisable(true);
        loadSupplierData.setDisable(true);
        searchfieldSuppliers.setDisable(true);
        searchfieldSuppliers.setText("");
        btnCleanSearchfieldSuppliers.setDisable(true);
        //if(supplierTableView != null) searchSupplier();

        supplierTableView.setDisable(true);
        supplierTableView.getSelectionModel().clearSelection();
    }

    private void deactivateProductGui() {
        //Deaktiviere alle anderen Felder
        reloadProductsButton.setDisable(true);
        productEdit.setDisable(true);
        productDelete.setDisable(true);
        loadSupplierData.setDisable(true);
        searchfieldProducts.setDisable(true);
        //searchfieldProducts.setText("");
        btnCleanSearchfieldProducts.setDisable(true);

        productTableView.setDisable(true);
        productTableView.getSelectionModel().clearSelection();
    }

    private void activateSupplierGui() {
        //Aktiviere alle Felder
        reloadSupplierButton.setDisable(false);
        supplierNew.setDisable(false);
        searchfieldSuppliers.setDisable(false);
        //btnCleanSearchfieldSuppliers.setDisable(false);
        supplierTableView.setDisable(false);

        if(this.productList != null && !productList.isEmpty()){
            //Button Lieferantendaten Laden aktivieren
            loadSupplierData.setDisable(false);
        }
    }

    private void activateProductGui() {
        //Aktiviere alle Felder
        reloadProductsButton.setDisable(false);
        searchfieldProducts.setDisable(false);
        //btnCleanSearchfieldProducts.setDisable(false);
        productTableView.setDisable(false);

        if(this.productList != null && !productList.isEmpty()){
            //Button Lieferantendaten Laden aktivieren
            loadSupplierData.setDisable(false);
        }

        if(searchfieldProducts.getText().length() > 0) btnCleanSearchfieldProducts.setDisable(false);
    }

    private void startProgress(String progressTitle){
        loadingProgressBar.setOpacity(1);
        loadingProgressBar.setProgress(0.1);
        progressBarTitle.setText(progressTitle);
    }

    private void endProgress(){
        progressBarTitle.setText("");
        loadingProgressBar.setOpacity(0);
        loadingProgressBar.setProgress(1);
    }

    public void setProgress(double progress) {
        this.loadingProgressBar.setProgress(progress);
    }

    public double getProgress() {
        return loadingProgressBar.getProgress();
    }

    public static void updateProgressBar() {
        if (instance != null) {
            instance.setProgress(instance.getProgress() + 0.1);
        }
    }

    public static void updateProgressBar(double addToProgress) {
        if (instance != null) {
            instance.setProgress(instance.getProgress() + addToProgress);
        }
    }

    //endregion
}