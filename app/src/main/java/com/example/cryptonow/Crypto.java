package com.example.cryptonow;

public class Crypto {
    private String symbol;
    private String lastPrice;
    private String priceChangePercent;
    private String iconUrl;
    private String marketCap;

    public Crypto(String symbol, String lastPrice, String priceChangePercent, String iconUrl, String marketCap) {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.priceChangePercent = priceChangePercent;
        this.iconUrl = iconUrl;
        this.marketCap = marketCap;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPriceChangePercent(String priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    @Override
    public String toString() {
        return "Crypto{" +
                "symbol='" + symbol + '\'' +
                ", lastPrice='" + lastPrice + '\'' +
                ", priceChangePercent='" + priceChangePercent + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", marketCap='" + marketCap + '\'' +
                '}';
    }
}