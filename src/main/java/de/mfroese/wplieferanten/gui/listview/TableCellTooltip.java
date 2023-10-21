package de.mfroese.wplieferanten.gui.listview;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class TableCellTooltip<S, T> extends TableCell<S, T> {

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn() {
        return param -> new TableCellTooltip<>();
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        setTooltip(null);

        if (empty || item == null) {
            setText(null);
        }else{
            setText(item.toString());

            if (item.toString().length() > 5) {
                setTooltip(new Tooltip(item.toString()));
            }
        }
    }
}