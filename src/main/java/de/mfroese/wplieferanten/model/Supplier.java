package de.mfroese.wplieferanten.model;

import javafx.beans.property.*;

/**
 * Datenklasse fuer einen einzelnen Lieferanten
 */
public class Supplier {

    //region 0. Konstanten
    public static final String DEFAULT_STRING_VALUE = "";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final double DEFAULT_DOUBLE_VALUE = 0.0;
    //endregion

    //region 1. Attribute
    private IntegerProperty id;
    private StringProperty company;
    private StringProperty website;
    private StringProperty contact;
    private StringProperty email;
    private StringProperty phone;
    private DoubleProperty vat;
    private DoubleProperty profit_margin;
    //endregion

    //region 2. Konstruktoren

    // Standardkonstruktor
    public Supplier() {
        id            = new SimpleIntegerProperty(DEFAULT_INT_VALUE);
        company       =  new SimpleStringProperty(DEFAULT_STRING_VALUE);
        website       =  new SimpleStringProperty(DEFAULT_STRING_VALUE);
        contact       =  new SimpleStringProperty(DEFAULT_STRING_VALUE);
        email         =  new SimpleStringProperty(DEFAULT_STRING_VALUE);
        phone         =  new SimpleStringProperty(DEFAULT_STRING_VALUE);
        vat           =  new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
        profit_margin =  new SimpleDoubleProperty(DEFAULT_DOUBLE_VALUE);
    }

    // Ueberladner Konstruktor
    public Supplier(int id, String company, String website, String contact, String email, String phone, double vat, double profit_margin) {
        this.id            = new SimpleIntegerProperty(id);
        this.company       =  new SimpleStringProperty(company);
        this.website       =  new SimpleStringProperty(website);
        this.contact       =  new SimpleStringProperty(contact);
        this.email         =  new SimpleStringProperty(email);
        this.phone         =  new SimpleStringProperty(phone);
        this.vat           =  new SimpleDoubleProperty(vat);
        this.profit_margin =  new SimpleDoubleProperty(profit_margin);
    }
    //endregion

    //region 3. Getter und Setter

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getCompany() {
        return company.get();
    }

    public StringProperty companyProperty() {
        return company;
    }

    public void setCompany(String company) {
        this.company.set(company);
    }

    public String getWebsite() {
        return website.get();
    }

    public StringProperty websiteProperty() {
        return website;
    }

    public void setWebsite(String website) {
        this.website.set(website);
    }

    public String getContact() {
        return contact.get();
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public double getVat() {
        return vat.get();
    }

    public DoubleProperty vatProperty() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat.set(vat);
    }

    public double getProfit_margin() {
        return profit_margin.get();
    }

    public DoubleProperty profit_marginProperty() {
        return profit_margin;
    }

    public void setProfit_margin(double profit_margin) {
        this.profit_margin.set(profit_margin);
    }

    //endregion

    //region 4. Methoden und Funktionen
    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id.get() +
                ", company='" + company.get() + '\'' +
                ", website='" + website.get() + '\'' +
                ", contact='" + contact.get() + '\'' +
                ", email='" + email.get() + '\'' +
                ", phone='" + phone.get() + '\'' +
                ", vat=" + vat.get() +
                ", profit_margin=" + profit_margin.get() +
                '}';
    }

    public String search() {
        return id.get() + "'" + company.get() + "'" + website.get() + "'" + contact.get() +
                "'" + email.get() + "'" + phone.get() + "'" + vat.get() + "'" + profit_margin.get();
    }
    //endregion

}