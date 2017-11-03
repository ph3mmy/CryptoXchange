package com.oluwafemi.cryptoxchange.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.oluwafemi.cryptoxchange.db.CurrencyDatabase;
import com.oluwafemi.cryptoxchange.db.CurrencyDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oluwaphemmy on 11-Oct-17.
 */

public class CurrencyViewModel extends AndroidViewModel {

    private final LiveData<List<CurrencyDb>> allCurrencyList;
    private CurrencyDatabase currencyDatabase;

    public CurrencyViewModel(Application application) {
        super(application);

        currencyDatabase = CurrencyDatabase.getDatabase(this.getApplication());
        allCurrencyList = currencyDatabase.currencyItemAndModelDao().getAllCurrency();
    }

    public LiveData<List<CurrencyDb>> getAllCurrencyList(){
        return allCurrencyList;
    }

    public void insertCurrency(ArrayList<CurrencyDb> currencyList) {
        new InsertAsyncTask(currencyList, currencyDatabase).execute();
    }

    public void deleteCurrency(CurrencyDb currencyDb) {
        new DeleteAsyncTask(currencyDatabase).execute(currencyDb);
    }

    public void getCurrencyByCode(String currencyCode, GetSingleCurrencyListener listener) {
        GetCurrencyAsyncTask cTask = new GetCurrencyAsyncTask(currencyDatabase);
        cTask.singleCurrencyListener = listener;
        cTask.execute(currencyCode);
    }


    public void destroyDatabaseInstance() {
        currencyDatabase.destroyInstance();
    }

    // get a currency asyncTask
    private static class GetCurrencyAsyncTask extends AsyncTask<String, Void, CurrencyDb> {

        private CurrencyDatabase db;
        private GetSingleCurrencyListener singleCurrencyListener;

        GetCurrencyAsyncTask(CurrencyDatabase db) {
            this.db = db;
        }

        @Override
        protected CurrencyDb doInBackground(String... params) {
            return db.currencyItemAndModelDao().getCurrencyByCode(params[0]);
        }

        @Override
        protected void onPostExecute(CurrencyDb currencyDb) {
            super.onPostExecute(currencyDb);
            singleCurrencyListener.onCurrencyLoaded(currencyDb);
        }
    }

    // delete asyncTask
    private static class DeleteAsyncTask extends AsyncTask<CurrencyDb, Void, Void> {

        private CurrencyDatabase db;

        DeleteAsyncTask(CurrencyDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final CurrencyDb... params) {
            db.currencyItemAndModelDao().deleteCurrency(params[0]);
            return null;
        }

    }

    private static class InsertAsyncTask extends AsyncTask<Void, Void, Void> {

        private CurrencyDatabase mDatabase;
        ArrayList<CurrencyDb> currencyDbList;

        InsertAsyncTask(ArrayList<CurrencyDb> currencyList, CurrencyDatabase db){
            this.currencyDbList = currencyList;
            mDatabase = db;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDatabase.currencyItemAndModelDao().addCurrencyListToDb(currencyDbList);
            return null;
        }
    }

}
