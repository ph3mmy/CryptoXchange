package com.oluwafemi.cryptoxchange.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.oluwafemi.cryptoxchange.utils.DateConverter;

/**
 * Created by Oluwaphemmy on 11-Oct-17.
 */

@Database(entities = {CurrencyDb.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class CurrencyDatabase extends RoomDatabase {

    private static CurrencyDatabase INSTANCE;

    public static CurrencyDatabase getDatabase(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CurrencyDatabase.class, "currency_db").build();
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        INSTANCE = null;
    }

    public abstract CurrencyDao currencyItemAndModelDao();
}
