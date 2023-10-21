package de.mfroese.wplieferanten.gui.treelistview;

import de.mfroese.wplieferanten.logic.Helpers;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import java.util.List;

public class TreeTableCellSupplierPriceRules<S> extends TreeTableCell<S, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null || item.isEmpty() || item.equals("a:0:{}")) {
            setText("");
            setGraphic(null);
        } else {
            setText(parsePriceRules(item));
        }
    }

    private String parsePriceRules(String priceRules) {
        List<String> priceInfoList = Helpers.priceRulesToReadebelList(priceRules);

        if (priceInfoList.size() > 1) {

            // Mehrere Preisinformationen vorhanden, zeige ComboBox an
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setItems(FXCollections.observableArrayList(priceInfoList));
            comboBox.setPrefWidth(200);

            // Ersten Eintrag als vordefinierten Wert auswählen
            comboBox.setValue(priceInfoList.get(0));

            setGraphic(comboBox);
            getStyleClass().add("comboBox-cell");
            return "";
        } else if (priceInfoList.size() == 1) {
            // Eine Preisinformation vorhanden, zeige sie als Text an
            setGraphic(null);
            return priceInfoList.get(0).toString();
        } else {
            // Keine Preisinformationen vorhanden
            setGraphic(null);
            return "";
        }
    }

}
