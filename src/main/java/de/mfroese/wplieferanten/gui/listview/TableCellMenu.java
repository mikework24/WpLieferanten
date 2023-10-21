package de.mfroese.wplieferanten.gui.listview;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;

public class TableCellMenu<S, T> extends TableCell<S, T> {

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn() {
        return param -> new TableCellMenu<>();
    }

    private final ContextMenu contextMenu;

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        setTooltip(null);
        setContextMenu(null);

        if (empty || item == null) {
            setText(null);
        }else{
            setText(item.toString());

            if (item.toString().length() > 0) setContextMenu(contextMenu);
            if (item.toString().length() > 5) setTooltip(new Tooltip(item.toString()));
        }
    }

    public TableCellMenu() {
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
}