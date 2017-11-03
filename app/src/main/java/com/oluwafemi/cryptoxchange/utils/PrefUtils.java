package com.oluwafemi.cryptoxchange.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Oluwaphemmy on 12-Oct-17.
 */

public class PrefUtils {

    private static final String BTC_PREF_KEY = "btc_rate_pref";
    private static final String ETH_PREF_KEY = "eth_rate_pref";
    private static final String TOOLBAR_HEIGHT = "toolbar_height";


    public static void saveBtcToPref(Context context, String btcRateString){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(BTC_PREF_KEY, btcRateString).apply();
    }

    public static String getBtcFromPref (Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(BTC_PREF_KEY, "");
    }

    public static void saveEthToPref(Context context, String ethRateString){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(ETH_PREF_KEY, ethRateString).apply();
    }
    public static String getEthFromPref (Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(ETH_PREF_KEY, "");
    }

    public static void setToolbarHeight(Context context, int toolbarHeight){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(TOOLBAR_HEIGHT, toolbarHeight).apply();
    }
    public static int getToolbarHeight (Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(TOOLBAR_HEIGHT, 0);
    }

}
