package de.mfroese.wplieferanten.gui.treelistview;

import de.mfroese.wplieferanten.model.Product;

import javafx.scene.control.*;

public class TreeTableCellStock<S, T> extends TreeTableCell<S, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            setText(String.valueOf(item));

            TreeTableRow<?> currentRow = getTableRow();
            if (currentRow != null) {
                Product currentProduct = (Product) currentRow.getItem();
                if(currentProduct != null){
                    String supplierStock = currentProduct.getSupplierStock();
                    String productStock = currentProduct.getStock();

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

                    //Auf unterschied zum haendler hinweisen
                    if ( !supplierStock.isEmpty() ){
                        //Vergleichsfarbe wenn ein Lieferanten Stock vorhanden ist
                        if ( supplierStock.equals(productStock) ) {
                            setStyle("-fx-background-color: #00ff0030;");
                        } else {
                            setStyle("-fx-background-color: #ff000030;");
                        }
                    }
                }

            }
        } else {
            setText(null);
            setStyle(null);
        }
    }
}