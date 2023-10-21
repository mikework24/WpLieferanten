package de.mfroese.wplieferanten.gui.treelistview;

import de.mfroese.wplieferanten.model.Product;
import de.mfroese.wplieferanten.logic.Helpers;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;
import javafx.scene.control.Tooltip;

public class TreeTableCellPrice<S, T> extends TreeTableCell<S, T> {

    public static <S, T> Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> forTreeTableColumn() {
        return param -> new TreeTableCellPrice<>();
    }

    private final ContextMenu contextMenu;

    public TreeTableCellPrice() {
        contextMenu = new ContextMenu();
        MenuItem copyMenuItem = new MenuItem("Kopieren");
        copyMenuItem.setOnAction(event -> copyToClipboard());
        contextMenu.getItems().add(copyMenuItem);
    }

    private void copyToClipboard() {
        String selectedText = getItem().toString();
        if (selectedText != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedText);
            clipboard.setContent(content);
        }
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        setStyle(null);
        setTooltip(null);
        setContextMenu(null);

        double calculatedNettoPrice = 0;
        double calculatedSalePrice = 0;

        //Faebung der Zelle
        if (!empty && item != null) {
            TreeTableRow<?> currentRow = getTableRow();
            if (currentRow != null) {
                Product currentProduct = (Product) currentRow.getItem();
                if(currentProduct != null)
                {
                    double supplierPrice = currentProduct.getSupplierPrice();
                    double price = currentProduct.getPrice();

                    if ( supplierPrice > 0.0 ){

                        calculatedNettoPrice = Helpers.supplierPriceNetto(supplierPrice, currentProduct.getSupplierId());
                        calculatedSalePrice = Helpers.nettoPriceWithMargin(supplierPrice, currentProduct.getSupplierId());

                        if(calculatedNettoPrice >= price){
                            setStyle("-fx-background-color: #ff000030;");
                        }else if( calculatedSalePrice >= price) {
                            setStyle("-fx-background-color: #ffff0030;");
                        }else{
                            setStyle("-fx-background-color: #00ff0030;");
                        }
                    }
                }
            }
        }


        if ( !(empty || item == null) ) {
            setText(String.format("%.2f €", item));
            getStyleClass().add("currency-cell");
            setContextMenu(contextMenu);

            if(calculatedSalePrice > 0){
                String toolTop = "Dein Preis: " + String.format("%.2f €", item) +
                               "\nBerechneter Preis: " + String.format("%.2f €", calculatedSalePrice);

                setTooltip(new Tooltip(toolTop));
            }
        }
    }
}