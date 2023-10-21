package de.mfroese.wplieferanten.model;

import javafx.beans.property.*;

/**
 * Datenklasse fuer ein einzelnes Produkt
 */
public class Product {

    //region 0. Konstanten
    public static final String DEFAULT_STRING_VALUE = "";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final double DEFAULT_DOUBLE_VALUE = 0.0;
    //endregion

    //region 1. Decl and Init Attribute
    private IntegerProperty id;
    private StringProperty title;
    private DoubleProperty price;
    private DoubleProperty regularPrice;
    private DoubleProperty mwSt;
    private StringProperty priceRules;
    private StringProperty stock;
    private StringProperty wpLink;
    private IntegerProperty parentId;
    private IntegerProperty supplierId;
    private StringProperty supplierUrl;
    private DoubleProperty supplierPrice;
    private StringProperty supplierPriceRules;
    private StringProperty supplierStock;
    //endregion

    //region 2. Konstruktoren
    /**
     * 1. Standardkonstruktor
     */
    public Product() {
        id                 = new SimpleIntegerProperty(DEFAULT_INT_VALUE);
        title              = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        price              = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        regularPrice       = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        mwSt               = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        priceRules         = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        stock              = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        wpLink             = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        parentId           = new SimpleIntegerProperty(DEFAULT_INT_VALUE);
        supplierId         = new SimpleIntegerProperty(DEFAULT_INT_VALUE);
        supplierUrl        = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        supplierPrice      = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        supplierPriceRules = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        supplierStock      = new SimpleStringProperty(DEFAULT_STRING_VALUE);
    }

    /**
     * 2. Ueberladner Konstruktor
     */
    public Product(int id, String title, Double price, Double regularPrice, Double mwSt, String priceRules,
                   String stock, String wpLink, int parentId, int supplierId, String supplierUrl) {
        this.id                 = new SimpleIntegerProperty(id);
        this.title              = new SimpleStringProperty(title);
        this.price              = new SimpleDoubleProperty(price);
        this.regularPrice       = new SimpleDoubleProperty(regularPrice);
        this.mwSt               = new SimpleDoubleProperty(mwSt);
        this.priceRules         = new SimpleStringProperty(priceRules);
        this.stock              = new SimpleStringProperty(stock);
        this.wpLink             = new SimpleStringProperty(wpLink);
        this.parentId           = new SimpleIntegerProperty(parentId);
        this.supplierId         = new SimpleIntegerProperty(supplierId);
        this.supplierUrl        = new SimpleStringProperty(supplierUrl);
        this.supplierPrice      = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        this.supplierPriceRules = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        this.supplierStock      = new SimpleStringProperty(DEFAULT_STRING_VALUE);
    }

    public Product(int id, double supplierPrice , String supplierPriceRules , String supplierStock) {
        this.id                 = new SimpleIntegerProperty(id);
        title                   = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        price                   = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        regularPrice            = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        mwSt                    = new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        priceRules              = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        stock                   = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        wpLink                  = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        parentId                = new SimpleIntegerProperty(DEFAULT_INT_VALUE);
        supplierId              = new SimpleIntegerProperty(DEFAULT_INT_VALUE);
        supplierUrl             = new SimpleStringProperty(DEFAULT_STRING_VALUE);
        this.supplierPrice      = new SimpleDoubleProperty(supplierPrice);
        this.supplierPriceRules = new SimpleStringProperty(supplierPriceRules);
        this.supplierStock      = new SimpleStringProperty(supplierStock);
    }
    //endregion

    //region 3. Getter und Setter
    //Getter

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public double getRegularPrice() {
        return regularPrice.get();
    }

    public DoubleProperty regularPriceProperty() {
        return regularPrice;
    }

    public double getMwSt() {
        return mwSt.get();
    }

    public DoubleProperty mwStProperty() {
        return mwSt;
    }

    public String getPriceRules() {
        return priceRules.get();
    }

    public StringProperty priceRulesProperty() {
        return priceRules;
    }

    public String getStock() {
        return stock.get();
    }

    public StringProperty stockProperty() {
        return stock;
    }

    public String getWpLink() {
        return wpLink.get();
    }

    public StringProperty wpLinkProperty() {
        return wpLink;
    }

    public int getParentId() {
        return parentId.get();
    }

    public IntegerProperty parentIdProperty() {
        return parentId;
    }

    public int getSupplierId() {
        return supplierId.get();
    }

    public IntegerProperty supplierIdProperty() {
        return supplierId;
    }

    public String getSupplierUrl() {
        return supplierUrl.get();
    }

    public StringProperty supplierUrlProperty() {
        return supplierUrl;
    }

    public double getSupplierPrice() {
        return supplierPrice.get();
    }

    public DoubleProperty supplierPriceProperty() {
        return supplierPrice;
    }

    public String getSupplierPriceRules() {
        return supplierPriceRules.get();
    }

    public StringProperty supplierPriceRulesProperty() {
        return supplierPriceRules;
    }

    public String getSupplierStock() {
        return supplierStock.get();
    }

    public StringProperty supplierStockProperty() {
        return supplierStock;
    }


    //Setter

    public void setId(int id) {
        this.id.set(id);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setRegularPrice(double regularPrice) {
        this.regularPrice.set(regularPrice);
    }

    public void setMwSt(double mwSt) {
        this.mwSt.set(mwSt);
    }

    public void setPriceRules(String priceRules) {
        this.priceRules.set(priceRules);
    }

    public void setStock(String stock) {
        this.stock.set(stock);
    }

    public void setWpLink(String wpLink) {
        this.wpLink.set(wpLink);
    }

    public void setParentId(int parentId) {
        this.parentId.set(parentId);
    }

    public void setSupplierId(int supplierId) {
        this.supplierId.set(supplierId);
    }

    public void setSupplierUrl(String supplierUrl) {
        this.supplierUrl.set(supplierUrl);
    }

    public void setSupplierPrice(double supplierPrice) {
        this.supplierPrice.set(supplierPrice);
    }

    public void setSupplierPriceRules(String supplierPriceRules) {
        this.supplierPriceRules.set(supplierPriceRules);
    }

    public void setSupplierStock(String supplierStock) {
        this.supplierStock.set(supplierStock);
    }


    //endregion

    //region 4. Methoden und Funktionen
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id.get() +
                ", title='" + title.get() + '\'' +
                ", price=" + price.get() +
                ", regularPrice=" + regularPrice.get() +
                ", mwSt=" + mwSt.get() +
                ", priceRules='" + priceRules.get() + '\'' +
                ", stock='" + stock.get() + '\'' +
                ", wpLink='" + wpLink.get() + '\'' +
                ", parentId='" + parentId.get() + '\'' +
                ", supplierId=" + supplierId.get() +
                ", supplierUrl='" + supplierUrl.get() + '\'' +
                ", supplierPrice=" + supplierPrice.get() +
                ", supplierPriceRules='" + supplierPriceRules.get() + '\'' +
                ", supplierStock='" + supplierStock.get() + '\'' +
                '}';
    }

    public String search() {
        return id.get() + "'" + title.get() + "'" + price.get() + "'" + stock.get() + "'" + parentId.get() + "'" +
                supplierId.get() + "'" + supplierUrl.get() + "'" + supplierPrice.get() + "'" + supplierStock.get();
    }
    //endregion

}