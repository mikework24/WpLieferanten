package de.mfroese.wplieferanten.gui.treelistview;

import de.mfroese.wplieferanten.logic.Helpers;
import de.mfroese.wplieferanten.model.Product;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import java.util.List;


public class TreeTableCellPriceRules<S> extends TreeTableCell<S, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setStyle("");
        //getStyleClass().clear();

        if (empty || item == null || item.isEmpty() || item.equals("a:0:{}")) {
            setText("");
            setGraphic(null);
        } else {
            getStyleClass().add("comboBox-cell");
            setText(parsePriceRules(item));
        }
    }

    private String parsePriceRules(String priceRules) {
        List<String> priceInfoList = Helpers.priceRulesToReadebelList(priceRules);

        //Beziehe die daten aus dem aktuellen Produkt
        int supplierId = 0;
        String supplierPriceRules = "";
        TreeTableRow<?> currentRow = getTableRow();
        if (currentRow != null) {
            Product currentProduct = (Product) currentRow.getItem();
            if(currentProduct != null)
            {
                supplierPriceRules = currentProduct.getSupplierPriceRules();
                supplierId = currentProduct.getSupplierId();
                if(supplierId != 0 && supplierPriceRules.length() > 7 &&
                        !Helpers.priceRulesWithMargin(supplierPriceRules, supplierId).equals(priceRules)
                ){

                    int compared = Helpers.comparePriceRulesWithMargin(priceRules, supplierPriceRules, supplierId);

                    if(compared == -1) setStyle("-fx-background-color: #ff000030;");
                    else if(compared == 1) setStyle("-fx-background-color: #00ff0030;");
                    else setStyle("-fx-background-color: #ffff0030;");

                    if(compared != 1){
                        priceInfoList.add("Empfohlen:");
                        priceInfoList.addAll(
                                Helpers.priceRulesToReadebelList(
                                        Helpers.priceRulesWithMargin( supplierPriceRules, supplierId )
                                )
                        );
                    }
                }
            }
        }

        if (priceInfoList.size() > 1) {

            // Mehrere Preisinformationen vorhanden, zeige ComboBox an
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setItems(FXCollections.observableArrayList(priceInfoList));
            comboBox.setPrefWidth(200);

            // Ersten Eintrag als vordefinierten Wert auswählen
            comboBox.setValue(priceInfoList.get(0));

            setGraphic(comboBox);

            return "";
        } else if (priceInfoList.size() == 1) {
            // Eine Preisinformation vorhanden, zeige sie als Text an
            setGraphic(null);
            setStyle("-fx-padding: 0 3 0 8;-fx-alignment: CENTER-LEFT;");
            return priceInfoList.get(0).toString();
        } else {
            // Keine Preisinformationen vorhanden
            setGraphic(null);
            return "";
        }
    }

}
