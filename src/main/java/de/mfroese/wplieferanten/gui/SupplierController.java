package de.mfroese.wplieferanten.gui;

import de.mfroese.wplieferanten.logic.SupplierHolder;
import de.mfroese.wplieferanten.logic.Helpers;
import de.mfroese.wplieferanten.model.ComboBoxItem;
import de.mfroese.wplieferanten.model.Supplier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

public class SupplierController {
    //region Konstanten
    //endregion

    //region Attribute

    @FXML
    private Text supplierTitle;

    @FXML
    private TextField companyField;
    @FXML
    private TextField websiteField;
    @FXML
    private ComboBox vatField;
    @FXML
    private TextField profitMarginField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private Supplier selectedSupplier;
    //endregion

    //region Controller verbinden
    private MainController mainController;

    // Methode zum Setzen des MainControllers
    public void mainController(MainController mainController) {
        this.mainController = mainController;
    }

    //endregion

    //region Konstruktoren
    @FXML
    public void initialize(){
        Platform.runLater(() -> {
            if(!mainController.newSupplierOption &&
                    mainController.supplierTableView != null &&
                    mainController.supplierTableView.getSelectionModel().getSelectedItem() != null){

                selectedSupplier = mainController.supplierTableView.getSelectionModel().getSelectedItem();
                supplierTitle.setText("Lieferant bearbeiten");
            }else{
                selectedSupplier = new Supplier();
                saveButton.setDisable(true);
            }

            //Im Preisfeld darf nur ein gueltiger wert sein
            profitMarginField.textProperty().addListener((observable, oldValue, newValue) -> {
                String enterdValue = newValue.replaceAll("[^0-9.]", "");
                String filteredValue = enterdValue.replace(",", ".");
                if (filteredValue.matches("^\\d*\\.?\\d*$")) {
                    profitMarginField.setText(enterdValue);
                } else {
                    profitMarginField.setText(oldValue);
                }
            });


            // Steuer (ComboBox) mit Optionen fuellen
            List<ComboBoxItem> vatOptions = Arrays.asList(
                    new ComboBoxItem("0.0", "Exklusive (netto)"),
                    new ComboBoxItem("19.0", "Inklusive (19%)")
            );
            vatField.getItems().setAll(vatOptions);
            vatField.setValue(Helpers.getComboBoxItemByValue(vatOptions, "19.0"));

            //fuelle die felder aus
            if (selectedSupplier != null) {
                companyField.setText(selectedSupplier.getCompany());
                websiteField.setText(selectedSupplier.getWebsite());
                contactField.setText(selectedSupplier.getContact());
                emailField.setText(selectedSupplier.getEmail());
                phoneField.setText(selectedSupplier.getPhone());
                profitMarginField.setText(String.valueOf(selectedSupplier.getProfit_margin()));

                vatField.setValue(Helpers.getComboBoxItemByValue(vatOptions, String.valueOf(selectedSupplier.getVat())));
            }


            //Deaktivieren des Speichern buttons wenn Unternehmen und Url leer ist
            companyField.textProperty().addListener((observable, oldValue, newValue) -> {saveButtonStatus();});
            websiteField.textProperty().addListener((observable, oldValue, newValue) -> {saveButtonStatus();});

        });

    }
    //endregion

    //region Methoden
    private void saveButtonStatus(){
        if (companyField.getText().length() > 2 && websiteField.getText().length() > 3) {
            saveButton.setDisable(false);
        } else {
            saveButton.setDisable(true);
        }
    }

    @FXML
    protected void closeWindow() {
        // Zugriff auf das aktuelle Fenster erhalten
        Stage stage = (Stage) cancelButton.getScene().getWindow();

        // Fenster schließen
        stage.close();
    }

    @FXML
    protected void changeProductAndCloseWindow() {

        // Daten im Objekt ablegen
        selectedSupplier.setCompany(companyField.getText());
        selectedSupplier.setWebsite(websiteField.getText());
        selectedSupplier.setContact(contactField.getText());
        selectedSupplier.setEmail(emailField.getText());
        selectedSupplier.setPhone(phoneField.getText());

        double vatDouble = 0.0;
        if(vatField.getValue() != null){
            String selectedVatValue = ((ComboBoxItem) vatField.getValue()).getValue();
            try {
                vatDouble = Double.parseDouble(selectedVatValue);
            } catch (NumberFormatException ignored) {}
            selectedSupplier.setVat(vatDouble);
        }

        double profitMarginDouble = 0.0;
        if(profitMarginField.getText() != null){
            try {
                profitMarginDouble = Double.parseDouble(profitMarginField.getText());
            } catch (NumberFormatException ignored) {}
        }
        selectedSupplier.setProfit_margin(profitMarginDouble);


        if(selectedSupplier.getId() == 0){
            // Neuen Lieferanten Anlegen
            SupplierHolder.getInstance().getSuppliers().add(selectedSupplier);
        }else{
            // Updatet vorhandenen Lieferanten in der Datenbank
            SupplierHolder.getInstance().update(selectedSupplier);
        }

        mainController.searchSupplier();

        //Tableview Aktuallisieren
        mainController.supplierTableView.refresh();

        // Fenster schließen
        closeWindow();
    }

    //endregion
}
