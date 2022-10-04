package com.example.tickertracker;

public class CompanyData {
    private String symbol;
    private String price;
    private String beta;
    private String volAvg;
    private String mktCap;
    private String lastDiv;
    private String range;
    private String changes;
    private String companyName;
    private String exchange;
    private String industry;
    private String website;
    private String description;
    private String ceo;
    private String sector;
    private String image;
    private String ipoDate;



    public CompanyData(  String symbol, String price, String beta, String volAvg, String mktCap,
                   String lastDiv, String range, String changes, String companyName, String exchange,
                   String industry, String website, String description, String ceo, String sector,
                   String image, String ipoDate) {

        this.symbol = symbol;
        this.exchange = exchange;
        this.description = description;
        this.ipoDate = ipoDate;
        this.industry = industry;
        this.sector = sector;
        this.ceo = ceo;
        this.website = website;
        this.price = price;
        this.beta = beta;
        this.volAvg = volAvg;
        this.mktCap = mktCap;
        this.lastDiv = lastDiv;
        this.range = range;
        this.changes = changes;
        this.image = image;
        this.companyName = companyName;

    }

    public String getName() {
        return companyName;
    }

    public String getExchange() {
        return exchange;
    }
    public String getDescription() {
        return description;
    }
    public String getIPO() {
        return ipoDate;
    }
    public String getindustry() {
        return industry;
    }

    public String getsector() {
        return sector;
    }

    public String getCEO() {
        return ceo;
    }

    public String getwebsite() {
        return website;
    }

    public String getcurrentPrice() {
        return price;
    }

    public String getbeta() {
        return beta;
    }

    public String getvolume() {
        return volAvg;
    }

    public String getmarket() {
        return mktCap;
    }

    public String getdividend() {
        return lastDiv;
    }

    public String getweek() {
        return range;
    }

    public String getImageURL() {
        return image;
    }
    public String getChanges() {
        return changes;
    }
}
