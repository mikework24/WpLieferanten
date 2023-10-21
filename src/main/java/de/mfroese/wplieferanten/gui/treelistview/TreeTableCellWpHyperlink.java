package de.mfroese.wplieferanten.gui.treelistview;

import de.mfroese.wplieferanten.model.Product;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import java.net.MalformedURLException;
import java.net.URL;

public class TreeTableCellWpHyperlink<S, T> extends TreeTableCell<S, T> {

    public TreeTableCellWpHyperlink() {
        setupContextMenu();
    }

    private void setupContextMenu() {
        MenuItem copyMenuItem = new MenuItem("Kopieren");
        copyMenuItem.setOnAction(event -> {
            String link = getItem().toString();

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(link);
            clipboard.setContent(content);
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(copyMenuItem);

        setContextMenu(contextMenu);

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        setGraphic(null);
        setTooltip(null);
        getStyleClass().remove("hyperlink-cell");

        if ( !(empty || item == null || item.toString().isEmpty()) ) {

            TreeTableRow<?> currentRow = getTableRow();
            if (currentRow != null) {
                Product currentProduct = (Product) currentRow.getItem();
                if(currentProduct != null)
                {
                    //Link umwandeln zum produkt bearbeiten
                    String Wplink = currentProduct.getWpLink();

                    try {
                        URL url = new URL(Wplink);
                        int produktId = currentProduct.getId();
                        if(currentProduct.getParentId() > 0){
                            produktId = currentProduct.getParentId();
                        }
                        Wplink = url.getProtocol() + "://" + url.getHost() + "/wp-admin/post.php?post=" + produktId + "&action=edit";
                    } catch (MalformedURLException e) {
                        //System.out.println("Ungültige URL: " + e.getMessage());
                    }

                    String WplinkToAdminProduct = Wplink;

                    Hyperlink hyperlink = new Hyperlink(item.toString());
                    hyperlink.setOnAction(event -> {
                        // Open the website URL in the browser
                        try {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI(WplinkToAdminProduct));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    setGraphic(hyperlink);
                    getStyleClass().add("hyperlink-cell");

                }
            }

        }
    }
}