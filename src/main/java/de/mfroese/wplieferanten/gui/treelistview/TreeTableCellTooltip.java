package de.mfroese.wplieferanten.gui.treelistview;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class TreeTableCellTooltip<S, T> extends TreeTableCell<S, T> {

    public static <S, T> Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> forTreeTableColumn() {
        return param -> new TreeTableCellTooltip<>();
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setTooltip(null);
        } else {
            setText(item.toString());

            if (item.toString().length() > 5) {
                setTooltip(new Tooltip(item.toString()));
            } else {
                setTooltip(null);
            }
        }
    }
}
