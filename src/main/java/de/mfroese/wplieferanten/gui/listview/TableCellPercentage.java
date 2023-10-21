package de.mfroese.wplieferanten.gui.listview;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableCellPercentage<S> extends TableCell<S, Double> {

    public static <S> Callback<TableColumn<S, Double>, TableCell<S, Double>> forTableColumn() {
        return param -> new TableCellPercentage<>();
    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(String.format("%.2f%%", item));
            getStyleClass().add("percentage-cell");
        }
    }
}