package de.mfroese.wplieferanten.gui.treelistview;

import javafx.scene.control.TreeTableCell;

public class TreeTableCellSupplierStock<S, T> extends TreeTableCell<S, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            setText(String.valueOf(item));

            if (item.equals("instock")) {
                setText("Lieferbar");
                setStyle("-fx-background-color: #00ff0030;");
            }else if (item.equals("outofstock")) {
                setText("Nicht vorrätig");
                setStyle("-fx-background-color: #ff000030;");
            }else if (item.equals("onbackorder")) {
                setText("Nicht verfügbar");
                setStyle("-fx-background-color: #ffff0030;");
            }else{
                setText(null);
                setStyle(null);
            }

        } else {
            setText(null);
            setStyle(null);
        }
    }
}