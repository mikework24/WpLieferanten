package de.mfroese.wplieferanten.gui.treelistview;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;

public class TreeTableCellHyperlink<S, T> extends TreeTableCell<S, T> {

    public TreeTableCellHyperlink() {
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

        if (empty || item == null || item.toString().isEmpty()) {
            setGraphic(null);
            setTooltip(null);
            getStyleClass().remove("hyperlink-cell");
        } else {
            String url = item.toString();
            String displayText = url.replace("https://", "").replace("http://", "");
            Hyperlink hyperlink = new Hyperlink(displayText);
            hyperlink.setOnAction(event -> {
                // Open the website URL in the browser
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            setGraphic(hyperlink);
            setTooltip(new Tooltip(url));
            getStyleClass().add("hyperlink-cell");
        }
    }
}