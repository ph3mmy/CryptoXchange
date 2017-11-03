package com.oluwafemi.cryptoxchange.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.oluwafemi.cryptoxchange.utils.DateConverter;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oluwaphemmy on 11-Oct-17.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface CurrencyDao {

    //query for all currency
    @Query("select * from CurrencyDb")
    LiveData<List<CurrencyDb>> getAllCurrency();

    //query for a particular currency with code
    @Query("select * from CurrencyDb where currencyCode = :currencyCode")
    CurrencyDb getCurrencyByCode(String currencyCode);

    @Query("select * from CurrencyDb")
    List<CurrencyDb> getCurrencyList();

    //if a particular currency exists
    @Query("select * from CurrencyDb where currencyCode = :currencyCode")
    CurrencyDb checkIfCurrencyExist(String currencyCode);

    // query to insert
    @Insert(onConflict = REPLACE)
    void addCurrencyToDb(CurrencyDb currencyDb);

    // query to insert list of currencies
    @Insert(onConflict = REPLACE)
    void addCurrencyListToDb(ArrayList<CurrencyDb> currencyDb);

    //query for a particular currency
    @Update(onConflict = REPLACE)
    void updateCurrency(CurrencyDb currencyDb);

    // query to delete
    @Delete
    void deleteCurrency(CurrencyDb currencyDb);
}
