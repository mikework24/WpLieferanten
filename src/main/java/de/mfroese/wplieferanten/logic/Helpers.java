package de.mfroese.wplieferanten.logic;

import de.mfroese.wplieferanten.model.ComboBoxItem;
import de.mfroese.wplieferanten.model.Product;
import de.mfroese.wplieferanten.model.Supplier;

import javafx.scene.control.TreeItem;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {

    //region Konstanten
    //endregion

    //region Attribute
    //endregion

    //region Konstruktoren
    //endregion

    //region Methoden

    public static double nettoPriceWithMargin(double supplierPrice, int supplierId) {
        double margin = 0.0;

        //Beziehe die ausgewiesene Steuer und Margin die in der Lieferantenliste hinterlegt ist
        List<Supplier> supplierList = SupplierHolder.getInstance().getSuppliers();
        for (Supplier supplier : supplierList) {

            int currentId = supplier.getId();
            if (currentId == supplierId) {
                margin = supplier.getProfit_margin();
            }
        }

        //Einkaufspreis Netto + Margin
        double calculatedSalePrice = supplierPrice/100*(100+margin);

        //Runden auf die 2 Nachkommastelle
        return Math.round(calculatedSalePrice * 100.0) / 100.0;
    }


    public static double supplierPriceNetto(double supplierPrice, int supplierId) {
        double vat = 0.0;

        //Beziehe die ausgewiesene Steuer und Margin die in der Lieferantenliste hinterlegt ist
        List<Supplier> supplierList = SupplierHolder.getInstance().getSuppliers();
        for (Supplier supplier : supplierList) {

            int currentId = supplier.getId();
            if (currentId == supplierId) {
                vat = supplier.getVat();
            }
        }

        //Einkaufspreis Netto
        double calculatedSalePrice = supplierPrice/(100+vat)*(100);

        //Runden auf die 2 Nachkommastelle
        return Math.round(calculatedSalePrice * 100.0) / 100.0;
    }

    public double removeVat(double priceWithVat, double vat) {

        //Einkaufspreis Netto
        double calculatedSalePrice = priceWithVat/(100+vat)*(100);

        //Runden auf die 2 Nachkommastelle
        return Math.round(calculatedSalePrice * 100.0) / 100.0;
    }

    public static ComboBoxItem getComboBoxItemByValue(List<ComboBoxItem> items, String value) {
        for (ComboBoxItem item : items) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }

    public static List<String> priceRulesToReadebelList(String priceRules) {
        List<String> resultList = new ArrayList<>();

        Matcher matcher = Pattern.compile("i:(\\d+);s:(\\d+):\"(\\d+\\.\\d+)\"").matcher(priceRules);

        while (matcher.find()) {
            int quantity = Integer.parseInt(matcher.group(1));
            double price = Double.parseDouble(matcher.group(3));
            resultList.add("ab " + quantity + " St. " + formatPrice(price));
        }

        return resultList;
    }

    /**
     * Gibt einen int wert zurück (-1 = verlust, 0 = zu wenig gewinn, 1 = Margin erfüllt)
     * @param priceRules String
     * @param supplierPriceRules String
     * @param supplierId int
     * @return int
     */
    public static int comparePriceRulesWithMargin(String priceRules, String supplierPriceRules, int supplierId){
        Matcher matcher = Pattern.compile("i:(\\d+);s:(\\d+):\"(\\d+\\.\\d+)\"").matcher(priceRules);
        Matcher supplierMatcher = Pattern.compile("i:(\\d+);s:(\\d+):\"(\\d+\\.\\d+)\"").matcher(supplierPriceRules);

        List<ComboBoxItem> priceList = new ArrayList<>();
        List<ComboBoxItem> supplierPriceList = new ArrayList<>();

        while (matcher.find()) {
            String quantity = matcher.group(1);
            String price = matcher.group(3);
            priceList.add( new ComboBoxItem(quantity, price));
        }

        while (supplierMatcher.find()) {
            String quantity = supplierMatcher.group(1);
            String price = supplierMatcher.group(3);
            supplierPriceList.add( new ComboBoxItem(quantity, price));
        }

        // ungleiche länge der listen kann schlecht verglichen werden
        if(supplierPriceList.size() != priceList.size()) return -1;

        int resultValue = 1;

        double supplierPriceNettoFaktor = supplierPriceNetto(1.0, supplierId);
        double salePriceWithMarginFaktor = nettoPriceWithMargin(1.0, supplierId);

        for (int i = 0; i < supplierPriceList.size(); i++) {
            ComboBoxItem supplierItem = supplierPriceList.get(i);
            ComboBoxItem item = priceList.get(i);

            // Vergleiche den Value
            if (!supplierItem.getValue().equals(item.getValue())) return -1;

            // Vergleiche den Preis (als Double-Wert)
            double supplierPrice = Double.parseDouble(supplierItem.getText());
            double price = Double.parseDouble(item.getText());

            if (price < supplierPrice * supplierPriceNettoFaktor) return -1;
            if (price < supplierPrice * salePriceWithMarginFaktor) resultValue = 0;
        }

        return resultValue;
    }

    public static String priceRulesWithMargin(String priceRules, int supplierId) {
        Matcher matcher = Pattern.compile("i:(\\d+);s:(\\d+):\"(\\d+\\.\\d+)\"").matcher(priceRules);

        StringBuilder result = new StringBuilder();
        int rules = 0;

        while (matcher.find()) {
            rules++;
            int quantity = Integer.parseInt(matcher.group(1));
            double price = Double.parseDouble(matcher.group(3));
            price = nettoPriceWithMargin(price, supplierId);
            result.append("i:").append(quantity).append(";s:").append(String.valueOf(price).length())
                    .append(":\"").append(price).append("\";");
        }
        //a:1:{i:22;s:5:"25.25";}
        return "a:" + rules + ":{" + String.valueOf(result) + "}";
    }

    public static String supplierPriceRulesNetto(String priceRules, int supplierId) {
        Matcher matcher = Pattern.compile("i:(\\d+);s:(\\d+):\"(\\d+\\.\\d+)\"").matcher(priceRules);

        StringBuilder result = new StringBuilder();
        int rules = 0;

        while (matcher.find()) {
            rules++;
            int quantity = Integer.parseInt(matcher.group(1));
            double price = Double.parseDouble(matcher.group(3));
            price = supplierPriceNetto(price, supplierId);
            result.append("i:").append(quantity).append(";s:").append(String.valueOf(price).length())
                    .append(":\"").append(price).append("\";");
        }
        //a:1:{i:22;s:5:"25.25";}
        return "a:" + rules + ":{" + String.valueOf(result) + "}";
    }


    private static String formatPrice(double price) {
        return String.format("%.2f €", price);
    }

    public static Product findProductById(int id, List<Product> productList) {
        for (Product product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public static Supplier findSupplierById(int id, List<Supplier> supplierList) {
        for (Supplier supplier : supplierList) {
            if (supplier.getId() == id) {
                return supplier;
            }
        }
        return null;
    }

    public static TreeItem<Product> findTreeItemByValue(TreeItem<Product> root, Product value) {
        if (root.getValue() == value) {
            return root;
        }

        for (TreeItem<Product> child : root.getChildren()) {
            TreeItem<Product> result = findTreeItemByValue(child, value);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    //endregion
}
