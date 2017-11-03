package com.oluwafemi.cryptoxchange.model;

import com.oluwafemi.cryptoxchange.db.CurrencyDb;

/**
 * Created by Oluwaphemmy on 27-Oct-17.
 */

public interface GetSingleCurrencyListener {
    void onCurrencyLoaded(CurrencyDb currencyDb);
}
