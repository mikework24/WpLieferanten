package de.mfroese.wplieferanten.gui.treelistview;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;


public class TreeTableCellSupplierPrice<S> extends TreeTableCell<S, Double> {

    public static <S> Callback<TreeTableColumn<S, Double>, TreeTableCell<S, Double>> forTreeTableColumn() {
        return param -> new de.mfroese.wplieferanten.gui.treelistview.TreeTableCellSupplierPrice<>();
    }

    private final ContextMenu contextMenu;

    public TreeTableCellSupplierPrice() {
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
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        setText("");
        setContextMenu(null);

        //Formatierung der Waehrung
        if ( !(empty || item == null) ) {
            if(item > 0.0){
                setText(String.format("%.2f €", item));
                getStyleClass().add("currency-cell");
                setContextMenu(contextMenu);
            }
        }


    }
}