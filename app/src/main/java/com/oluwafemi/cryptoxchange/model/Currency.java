package com.oluwafemi.cryptoxchange.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Oluwaphemmy on 10-Oct-17.
 *
 * Class to model the data of the local currency.json file
 */

public class Currency {

    @SerializedName("symbol")
    private String symbol;
    @SerializedName("name")
    private String name;
    @SerializedName("symbol_native")
    private String symbol_native;
    @SerializedName("decimal_digits")
    private int decimal_digits;
    @SerializedName("rounding")
    private float rounding;
    @SerializedName("code")
    private String code;
    @SerializedName("name_plural")
    private String name_plural;

    @SerializedName("currencies")
    public ArrayList<Currency> currencyList;

    public Currency(String symbol, String name, String symbol_native, int decimal_digits, int rounding, String code, String name_plural) {
        this.symbol = symbol;
        this.name = name;
        this.symbol_native = symbol_native;
        this.decimal_digits = decimal_digits;
        this.rounding = rounding;
        this.code = code;
        this.name_plural = name_plural;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol_native() {
        return symbol_native;
    }

    public void setSymbol_native(String symbol_native) {
        this.symbol_native = symbol_native;
    }

    public int getDecimal_digits() {
        return decimal_digits;
    }

    public void setDecimal_digits(int decimal_digits) {
        this.decimal_digits = decimal_digits;
    }

    public float getRounding() {
        return rounding;
    }

    public void setRounding(int rounding) {
        this.rounding = rounding;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName_plural() {
        return name_plural;
    }

    public void setName_plural(String name_plural) {
        this.name_plural = name_plural;
    }
}