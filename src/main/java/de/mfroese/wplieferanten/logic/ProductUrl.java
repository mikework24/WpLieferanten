package de.mfroese.wplieferanten.logic;

import de.mfroese.wplieferanten.model.Product;
import de.mfroese.wplieferanten.model.Supplier;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.mfroese.wplieferanten.logic.Helpers;

import static de.mfroese.wplieferanten.logic.Helpers.supplierPriceNetto;

public class ProductUrl {

    // Helpers.removeVat(preis , 19)

    private int    id              = 0;
    private String url             = "";
    private double price           = 0.0;
    private double regularPrice    = 0.0;
    private double mwSt            = 0.0;
    private String fixedPriceRules = "";
    private String stockStatus     = "";
    private int supplierId         = 0;
    private double supplierVat     = 0.0;


    public ProductUrl(Product product) {
        id              = product.getId();
        url             = product.getSupplierUrl();
        supplierId      = product.getSupplierId();

        List<Supplier> supplierList = SupplierHolder.getInstance().getSuppliers();
        Supplier supplier = Helpers.findSupplierById(product.getSupplierId(), supplierList);
        supplierVat = supplier.getVat();

        if(url.length() > 0){

            //region Daten von einer Url Abrufen
            Document doc = null;
            try {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }

                int timeoutMillis = 10000; // 10 Sekunden
                doc = Jsoup.connect(url).timeout(timeoutMillis).get();
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("-> Fehler bei der Url: " + url + " <-");
            }
            //endregion

            //Website wurde geladen
            if(doc != null){

                //region MwSt
                /*
                double vatResult = 0.0;

                vatResult = vatCheckOne(doc);
                if(vatResult != 0.0) mwSt = vatResult;
                 */
                //endregion


                //region Stock Status
                String stockStatusResult = "";

                //<meta property="product:availability" content="instock" />
                stockStatusResult = stockFromMeta(doc);
                if(!stockStatusResult.isBlank()) stockStatus = stockStatusResult;


                //<link itemprop="availability" href="http://schema.org/InStock">
                if(stockStatus.isBlank()){
                    stockStatusResult = stockFromLink(doc);
                    if(!stockStatusResult.isBlank()) stockStatus = stockStatusResult;
                }

                // reinigungsberater.de
                if(stockStatus.isBlank()){
                    stockStatusResult = stockFromReinigungsberater(doc);
                    if(!stockStatusResult.isBlank()) stockStatus = stockStatusResult;
                }


                //endregion


                //region Preis
                Double priceResult = 0.0;

                //<meta property="product:price:amount" content="22.67">
                priceResult = priceAmountFromMeta(doc);
                if(priceResult > 0.0) price = priceResult;

                //<meta property="product:price" content="21,56">
                if(price == 0.0){
                    priceResult = priceFromMeta(doc);
                    if(priceResult > 0.0) price = priceResult;
                }

                //reinigungsberater.de
                if(price == 0.0){
                    priceResult = priceFromReinigungsberater(doc);
                    if(priceResult > 0.0) price = priceResult;
                }




                //fehler der www.toilettenpapier-24.de korregieren
                if(!url.contains("toilettenpapier-24")) {

                    //falls netto preise ausgewiesen wurden in netto umwandeln
                    price = supplierPriceNetto(price , supplierId);
                }
                //endregion



                //region Preis Table
                String PriceRulesResult = "";


                // Industrieputzpapier.de
                PriceRulesResult = priceRuesFromIndustrieputzpapier(doc);
                if(!PriceRulesResult.isBlank()) fixedPriceRules = PriceRulesResult;

                //todo preis * anzahl rechnen -> Packetpreise
                // www.hamburgpapier-shop.de
                if(fixedPriceRules.length() < 7){
                    PriceRulesResult = priceRuesFromHamburgpapier(doc);
                    if(!PriceRulesResult.isBlank()) fixedPriceRules = PriceRulesResult;
                }

                // www.toilettenpapier-24.de
                if(fixedPriceRules.length() < 7){
                    PriceRulesResult = priceRuesFromToilettenpapier(doc);
                    if(!PriceRulesResult.isBlank()) fixedPriceRules = PriceRulesResult;
                }

                // reinigungsberater.de
                if(fixedPriceRules.length() < 7){
                    PriceRulesResult = priceRuesFromReinigungsberater(doc);
                    if(!PriceRulesResult.isBlank()) fixedPriceRules = PriceRulesResult;
                }


                //falls brutto, in netto umwandeln
                fixedPriceRules = Helpers.supplierPriceRulesNetto(fixedPriceRules , supplierId);
                //endregion

            }
        }

        //System.out.println(id + " " + title + " " + price + " " + regularPrice + " " + mwSt + " " + fixedPriceRules + " " + stockStatus + " " + supplierId + " " + supplierUrl);

    }

    public Product get(){
        // Daten innerhalb einer produktlist zurueckgeben
        return new Product(id, price , fixedPriceRules , stockStatus);
    }




    //region Steuern: Vat
    public double vatCheckOne(Document doc){
        double vat = 0.0;
        Element mwStElement = doc.selectFirst("product-detail-tax");
        if (mwStElement != null) {
            if( mwStElement.text().toLowerCase().contains("inkl") ){
                vat = 19.0;
            }
        }
        return vat;
    }
    //endregion



    //region Preis
    public Double priceAmountFromMeta(Document doc){
        //<meta property="product:price:amount" content="22.67">
        double price = 0.0;
        Element metaProductPriceAmount = doc.selectFirst("meta[property=product:price:amount]");
        if(metaProductPriceAmount != null){
            String priceText = metaProductPriceAmount.attr("content");

            priceText = priceText.replace(",", ".").replaceAll("[^0-9.]", "");
            price            = Double.parseDouble(priceText);
        }
        return price;
    }


    public Double priceFromMeta(Document doc){
        //<meta property="product:price" content="21,56">
        double price = 0.0;
        Element metaProductPrice = doc.selectFirst("meta[property=product:price]");
        if(metaProductPrice != null){
            String priceText = metaProductPrice.attr("content");
            priceText = priceText.replace(",", ".").replaceAll("[^0-9.]", "");
            price            = Double.parseDouble(priceText);
        }
        return price;
    }


    public Double priceFromReinigungsberater(Document doc){
        // reinigungsberater.de
        double price = 0.0;
        Element reinigungsberaterPreisBasis = doc.selectFirst(".artikel_container_rechts .preis_basis");
        Element reinigungsberaterPreisDezimal = doc.selectFirst(".artikel_container_rechts .preis_decimal");
        if(reinigungsberaterPreisBasis != null && reinigungsberaterPreisDezimal != null){
            String priceText = reinigungsberaterPreisBasis.text() + reinigungsberaterPreisDezimal.text();
            priceText = priceText.replace(",", ".").replaceAll("[^0-9.]", "");
            try{
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return price;
    }


    //endregion



    //region Price Rules
    public String priceRuesFromIndustrieputzpapier(Document doc){
        //<meta itemprop="offerCount" content="4">
        Element offerCountElement = doc.selectFirst("meta[itemprop=offerCount]");
        if (offerCountElement != null) {

            //Wie viele angebotspreise gibt es?
            String offerCounts = offerCountElement.attr("content");
            int offerCount = Integer.parseInt(offerCounts);

            //Wenn das erste ein bis preis hat und es mehrere angebotspreise gibt wird das der erste eintrag ausgelassen
            Element firstOfferRow = doc.selectFirst("tbody.product-block-prices-body tr.product-block-prices-row");
            if(firstOfferRow != null){
                String quantityTypeText = firstOfferRow.text().toLowerCase();
                if (quantityTypeText.contains("bis") && offerCount > 1) {
                    offerCount = offerCount - 1;
                }
            }


            Elements priceRows = doc.select("tbody.product-block-prices-body tr.product-block-prices-row");
            StringBuilder priceTableString = new StringBuilder("a:" + offerCount + ":{");
            int previousQuantity = 0;

            for (Element row : priceRows) {
                Element quantityType = row.selectFirst("th.product-block-prices-cell-thin");
                Element priceElement = row.selectFirst("div");
                String priceValue = "";
                String quantityValue = "";

                //preis string wird vorbereitet fuer ein double
                if (priceElement != null) {
                    priceValue = priceElement.text().replace(",", ".").replaceAll("[^0-9.]", "");
                }

                if (quantityType != null) {
                    String quantityTypeText = quantityType.text().toLowerCase();
                    if (quantityTypeText.contains("bis")) {
                        int currentQuantity = previousQuantity + 1;
                        quantityValue = String.valueOf(currentQuantity);
                        previousQuantity = Integer.parseInt(Objects.requireNonNull(row.selectFirst("span.product-block-prices-quantity")).text());
                    } else {
                        quantityValue = Objects.requireNonNull(row.selectFirst("span.product-block-prices-quantity")).text();
                        previousQuantity = Integer.parseInt(quantityValue);
                    }
                }

                //wenn kein preis vorhanden ist kann der aus der tabelle genommen werden
                if(!priceValue.equals("") && quantityValue.equals("1")){

                    //falls netto preise ausgewiesen wurden in netto umwandeln
                    price = Double.parseDouble(priceValue);
                    price = supplierPriceNetto(price , supplierId);
                }


                //ab 1 produkt soll nicht in der preistabelle vorkommen da es woocomerce automatisch hinzufuegt
                if( !quantityValue.equals("1") ){
                    priceTableString.append("i:").append(quantityValue).append(";s:").append(priceValue.length()).append(":\"").append(priceValue).append("\";");
                }
            }

            priceTableString.append("}");

            return priceTableString.toString();
        }

        return "";
    }


    public String priceRuesFromHamburgpapier(Document doc){
        // www.hamburgpapier-shop.de

        Elements rows = doc.select("tbody.block-prices--body tr"); // Wähle die Tabellenzeilen aus
        if(rows.size() > 0){

            StringBuilder result = new StringBuilder("a:");

            result.append(rows.size()).append(":{");

            for (Element row : rows) {
                Elements cells = row.select("td");

                int anzahl = Integer.parseInt(cells.get(0).select("span.block-prices--quantity").text());
                String preisString = cells.get(1).text().replace(",", ".").replaceAll("[^0-9.]", "");
                //double preis = Double.parseDouble(preisString);

                result.append("i:").append(anzahl).append(";s:").append(preisString.length()).append(":\"")
                        .append(preisString).append("\";");
            }

            result.append("}");

            return result.toString();

        }

        return "";
    }


    public String priceRuesFromToilettenpapier(Document doc){
        Elements rows = doc.select("table.bm-bulk-table tbody tr"); // Wähle die Tabellenzeilen aus
        if(rows.size() > 0){

            StringBuilder result = new StringBuilder("a:");

            result.append(rows.size()).append(":{");

            for (Element row : rows) {
                Elements cells = row.select("td");

                int anzahl = Integer.parseInt(cells.get(1).text());
                String preisString = cells.get(0).text().replace(",", ".").replaceAll("[^0-9.]", "");
                //double preis = Double.parseDouble(preisString);

                result.append("i:").append(anzahl).append(";s:").append(preisString.length()).append(":\"")
                        .append(preisString).append("\";");
            }

            result.append("}");

            return result.toString();
        }
        return "";
    }


    public String priceRuesFromReinigungsberater(Document doc){

        Elements rows = doc.select(".artikel_container_rechts .wrapper_staffelbox .wrapper_staffelzeile");
        if(rows.size() > 0){

            StringBuilder result = new StringBuilder("a:");

            result.append(rows.size()).append(":{");

            for (Element row : rows) {
                String anzahl = row.select("strong").text();
                String preisString = row.select("span.preis_staffelpreis").text().replace(",", ".").replaceAll("[^0-9.]", "");

                //wenn kein preis vorhanden ist kann der aus der tabelle genommen werden
                if(!preisString.equals("") && anzahl.equals("1")){
                    //falls netto preise ausgewiesen wurden in netto umwandeln
                    price = Double.parseDouble(preisString);
                    price = supplierPriceNetto(price , supplierId);

                    result.setLength(0);
                    result.append("a:").append(rows.size()-1).append(":{");
                }else{

                    //alle Staffelpreise bis auf den ersten auflisten
                    result.append("i:").append(anzahl).append(";s:").append(preisString.length()).append(":\"")
                            .append(preisString).append("\";");
                }

            }

            result.append("}");

            return result.toString();
        }
        return "";
    }

    //endregion



    //region Stock Status
    public String stockFromMeta(Document doc){
        //<meta property="product:availability" content="instock" />
        Element metaInStock = doc.selectFirst("meta[property=product:availability]");
        if(metaInStock != null){
            return metaInStock.attr("content");
        }
        return "";
    }

    public String stockFromLink(Document doc){
        //<link itemprop="availability" href="http://schema.org/InStock">
        Element linkInStock = doc.selectFirst("link[itemprop=availability]");
        if(linkInStock != null){
            String inStockText = linkInStock.attr("href");
            if(inStockText.contains("InStock")){
                return "instock";
            }
        }
        return "";
    }

    public String stockFromReinigungsberater(Document doc){
        // reinigungsberater.de
        Element reinigungsberaterStock = doc.selectFirst(".artikel_container_rechts .aktion_content_liefer");
        if(reinigungsberaterStock != null){
            String localStockStatus = reinigungsberaterStock.text().toLowerCase();

            if(localStockStatus.contains("sofort lieferbar")){
                return "instock";
            }
        }
        return "";
    }


    //endregion


}
