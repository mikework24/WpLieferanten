package de.mfroese.wplieferanten.model;

public class ComboBoxItem {
    private final String value;
    private final String text;

    public ComboBoxItem(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}