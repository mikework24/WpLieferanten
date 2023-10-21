package de.mfroese.wplieferanten.gui.treelistview;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;


public class TreeTableCellMenu<S, T> extends TreeTableCell<S, T> {

    public static <S, T> Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> forTreeTableColumn() {
        return param -> new TreeTableCellMenu<>();
    }

    private final ContextMenu contextMenu;

    public TreeTableCellMenu() {
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

        if (empty || item == null) {
            setText(null);
            setTooltip(null);
            setContextMenu(null);
        } else {
            setText(item.toString());
            setTooltip(new Tooltip(item.toString()));
            setContextMenu(contextMenu);
        }
    }
}