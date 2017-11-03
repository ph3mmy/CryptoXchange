package com.oluwafemi.cryptoxchange.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.oluwafemi.cryptoxchange.utils.DateConverter;

import java.util.Date;

/**
 * Created by Oluwaphemmy on 11-Oct-17.
 */

@Entity
public class CurrencyDb {

//    @PrimaryKey(autoGenerate = true)
    public int id;
    @PrimaryKey
    private String currencyCode;
    private String currencyName;
    private String currencySymbol;
    private Double btcCurrencyRate;
    private Double invBtcrate;
    private Double ethCurrencyRate;
    private Double invEthRate;
    @TypeConverters(DateConverter.class)
    private Date lastUpdatedDate;

    public CurrencyDb(String currencyCode, String currencyName, String currencySymbol,Double btcCurrencyRate, Double invBtcrate, Double ethCurrencyRate,
                      Double invEthRate, Date lastUpdatedDate) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.btcCurrencyRate = btcCurrencyRate;
        this.invBtcrate = invBtcrate;
        this.ethCurrencyRate = ethCurrencyRate;
        this.invEthRate = invEthRate;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Double getBtcCurrencyRate() {
        return btcCurrencyRate;
    }

    public void setBtcCurrencyRate(Double btcCurrencyRate) {
        this.btcCurrencyRate = btcCurrencyRate;
    }

    public Double getInvBtcrate() {
        return invBtcrate;
    }

    public void setInvBtcrate(Double invBtcrate) {
        this.invBtcrate = invBtcrate;
    }

    public Double getEthCurrencyRate() {
        return ethCurrencyRate;
    }

    public void setEthCurrencyRate(Double ethCurrencyRate) {
        this.ethCurrencyRate = ethCurrencyRate;
    }

    public Double getInvEthRate() {
        return invEthRate;
    }

    public void setInvEthRate(Double invEthRate) {
        this.invEthRate = invEthRate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
