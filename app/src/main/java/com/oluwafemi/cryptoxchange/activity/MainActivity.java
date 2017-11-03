package com.oluwafemi.cryptoxchange.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.oluwafemi.cryptoxchange.R;
import com.oluwafemi.cryptoxchange.adapter.PagerAdapter;
import com.oluwafemi.cryptoxchange.db.CurrencyDb;
import com.oluwafemi.cryptoxchange.fragment.BtcFragment;
import com.oluwafemi.cryptoxchange.fragment.EthFragment;
import com.oluwafemi.cryptoxchange.io.AppSingleton;
import com.oluwafemi.cryptoxchange.model.Currency;
import com.oluwafemi.cryptoxchange.model.CurrencyViewModel;
import com.oluwafemi.cryptoxchange.utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BtcFragment.UpdateCurrencyCodeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BTC_API_URL = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=";
    private static final String ETH_API_URL = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    List<String> mList;
    ArrayList<Currency> currencyList;
    CurrencyViewModel currencyViewModel;
    ArrayList<String> existingCurrencyCode;
    List<CurrencyDb> mCurrencyList;
    SwipeRefreshLayout refreshLayout;
    AlertDialog dialog;
    private String persistedData;

    @Override
    protected void onStart() {
        super.onStart();
        persistedData = PrefUtils.getBtcFromPref(this);
        if (TextUtils.isEmpty(persistedData) && !isDeviceConnected()) {
            if (dialog == null) {
                dialog = showNetworkDialog();
                dialog.show();
            }
        }
    }

    private AlertDialog showNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("An Error Occurred");
        builder.setMessage("Cannot download Rate, Check your Internet connection or go to SETTINGS to turn it ON");
        builder.setCancelable(false);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });

        return builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.mipmap.ic_logo);
        }

        currencyViewModel = ViewModelProviders.of(this).get(CurrencyViewModel.class);

        // getting list of already existing currency to aid updating them
        mCurrencyList = new ArrayList<>();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCurrencyCard();
            }
        });
        prepareListFromJsonFile();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDeviceConnected() || !TextUtils.isEmpty(persistedData)) {
            prepareListFromJsonFile();
        } else {
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currencyViewModel.destroyDatabaseInstance();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void prepareListFromJsonFile() {
        mList = getCurrencyNameFromJson();
    }

    // setup for viewPager
    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BtcFragment(), "BITCOIN");
        adapter.addFragment(new EthFragment(), "ETHEREUM");
        viewPager.setAdapter(adapter);
    }

    // setup tablayout to accept text and icons using a custom view
    private void setupTabIcons() {
            View tabBtc = LayoutInflater.from(this).inflate(R.layout.tab_custom_text, null);
            ImageView imageView = tabBtc.findViewById(R.id.ivTabIcon);
            TextView title = tabBtc.findViewById(R.id.tvTabText);
            imageView.setImageResource(R.drawable.ic_tab_btc);
            title.setText(R.string.btc_string);
            tabLayout.getTabAt(0).setCustomView(tabBtc);

            View tabEth = LayoutInflater.from(this).inflate(R.layout.tab_custom_text, null);
            imageView = tabEth.findViewById(R.id.ivTabIcon);
            title = tabEth.findViewById(R.id.tvTabText);
            imageView.setImageResource(R.drawable.ic_tab_eth);
            title.setText(R.string.eth_string);
            tabLayout.getTabAt(1).setCustomView(tabEth);

            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
            tabLayout.setSelectedTabIndicatorHeight(10);

    }

    //method to launch dialog on clicking fab
    private void addCurrencyCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Currency to add");
        builder.setCancelable(false);

        //prepare json as arrays/list
        final String[] mArr = mList.toArray(new String[0]);
        final ArrayList<Integer> mChecked = new ArrayList<>();

        builder.setMultiChoiceItems(mArr, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    mChecked.add(which);
                } else if (mChecked.contains(which)) {
                    mChecked.remove(Integer.valueOf(which));
                }
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mChecked.size() <= 0) {
                    Toast.makeText(MainActivity.this, "No currency was selected", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isInserted = insertCurrencyToDb(currencyList, mChecked);
                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Currency Card successfully created", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //method to read InputStream and convert to string
    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    //fetch currency list from json file
    private List<String> getCurrencyNameFromJson() {
        String currencyJsonString = inputStreamToString(getResources().openRawResource(R.raw.currencies));
        Currency mCurrency = new Gson().fromJson(currencyJsonString, Currency.class);
        currencyList = mCurrency.currencyList;
        ArrayList<String> nameList = new ArrayList<>();
        List<String> codeList = new ArrayList<>();

        for (int i = 0; i < currencyList.size(); i++) {
            nameList.add(currencyList.get(i).getName_plural());
            codeList.add(currencyList.get(i).getCode());
        }
        downloadAndSaveRate(codeList);
        return nameList;
    }

    // method to form the Api url and make internet calls
    private void downloadAndSaveRate(List<String> cList) {
        if (isDeviceConnected()) {
            StringBuilder sBuilder = new StringBuilder();
            for (String mm : cList) {
                sBuilder.append(mm);
                sBuilder.append(",");
            }
            String commaSeparatedCode = sBuilder.toString();
            String formattedString = commaSeparatedCode.substring(0, commaSeparatedCode.length() - 1);

            // formed url
            String btc_url = BTC_API_URL + formattedString;
            String eth_url = ETH_API_URL + formattedString;

            fetchRatesWithVolley(btc_url, this, cList, "BTC");
            fetchRatesWithVolley(eth_url, this, cList, "ETH");
        }

    }

    private void fetchRatesWithVolley(String url, Context context, final List<String> cList, final String type) {

        // using the volley library to make network request
        JsonObjectRequest req = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseAndPersistJsonResponse(response, cList, type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error, could not update currencies", Toast.LENGTH_SHORT).show();
            }
        });

        AppSingleton.getInstance(context).addToRequestQueue(req, TAG);
    }

    // parse the response from volley and save sharedPreferences
    private void parseAndPersistJsonResponse(JSONObject res, List<String> codeList, String type) {

        JSONArray arr = new JSONArray();
        for (String code : codeList) {
            JSONObject obj = new JSONObject();
            try {
                double rate = res.getDouble(code);
                obj.put("code", code);
                obj.put("rate", rate);
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (type.equalsIgnoreCase("BTC")) {
            PrefUtils.saveBtcToPref(this, arr.toString());
        } else {
            PrefUtils.saveEthToPref(this, arr.toString());
        }
    }

    private boolean insertCurrencyToDb(ArrayList<Currency> currencyList, ArrayList<Integer> mChecked) {
        ArrayList<CurrencyDb> currencyDbArrayList = new ArrayList<>();

        String btcRatePrefs = PrefUtils.getBtcFromPref(MainActivity.this);
        String ethRatePrefs = PrefUtils.getEthFromPref(MainActivity.this);
        if ((!TextUtils.isEmpty(btcRatePrefs)) && (!TextUtils.isEmpty(ethRatePrefs))) {
            if (mChecked.size() > 0) {

                for (int i = 0; i < mChecked.size(); i++) {

                    // get currency Name, code and symbol
                    String currencyCode = currencyList.get(mChecked.get(i)).getCode();
                    String currencyName = currencyList.get(mChecked.get(i)).getName();
                    String currencySym = currencyList.get(mChecked.get(i)).getSymbol();

                    // get Btc Rate & inverse for a particular code
                    Map<String, Double> btcRateMap = loadMap(btcRatePrefs);
                    Double btcRate = btcRateMap.get(currencyCode);
                    Double invBtcRate = 1 / btcRate;

                    // get Eth Rate & inverse for a particular code
                    Map<String, Double> ethRateMap = loadMap(ethRatePrefs);
                    Double ethRate = ethRateMap.get(currencyCode);
                    Double invEthRate = 1 / ethRate;

                    // get present date and time
                    Date mDate = new Date(System.currentTimeMillis());

                    CurrencyDb currencyDb = new CurrencyDb(currencyCode, currencyName,
                            currencySym, btcRate, invBtcRate, ethRate, invEthRate, mDate);

                    // add new currencyDb item to arraylist
                    currencyDbArrayList.add(currencyDb);
                }
                // insert the new currency to db
                currencyViewModel.insertCurrency(currencyDbArrayList);
                return true;
            }
        }
        return false;
    }

    private Map<String, Double> loadMap(String prefData) {
        Map<String, Double> outputMap = new HashMap<>();
        try {
            JSONArray mArr = new JSONArray(prefData);
            for (int i = 0; i < mArr.length(); i++) {
                JSONObject obj = mArr.getJSONObject(i);
                outputMap.put(obj.getString("code"), obj.getDouble("rate"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    // method that gives access to the fab for easily manipulation
    public FloatingActionButton getFab() {
        return fab;
    }

    private boolean getExistingCurrenciesInDb(List<CurrencyDb> mCurrencys) {
        existingCurrencyCode = new ArrayList<>();
        for (CurrencyDb cDb : mCurrencys) {
            String existingCode = cDb.getCurrencyCode();
            existingCurrencyCode.add(existingCode);
        }
        ArrayList<Integer> existingCodePosition = getCodePositionList(existingCurrencyCode);
        return insertCurrencyToDb(currencyList, existingCodePosition);

    }

    public void updateCurrencyRates(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;

        if (isDeviceConnected()) {
            // download new rates and persist
            prepareListFromJsonFile();

            // update already persisted data
            boolean isUpdateSuccess = getExistingCurrenciesInDb(mCurrencyList);
            if (isUpdateSuccess) {
                refreshLayout.setRefreshing(false);
            } else {
                refreshLayout.setRefreshing(false);
            }
            Toast.makeText(MainActivity.this, "Currency rates successfully Updated", Toast.LENGTH_SHORT).show();

        } else {
            refreshLayout.setRefreshing(false);
            Toast.makeText(this, "Cannot update rates, Check your Internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Integer> getCodePositionList(ArrayList<String> codeList) {
        ArrayList<Integer> mInt = new ArrayList<>();
        for (int i = 0; i < codeList.size(); i++) {
            String code = codeList.get(i);
            int w = currencyCodePositionToList(code, currencyList);
            mInt.add(w);
        }
        return mInt;

    }

    private Integer currencyCodePositionToList(String code, ArrayList<Currency> list) {
        int m = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCode().equalsIgnoreCase(code)) {
                m = i;
            }
        }
        return m;
    }

    private boolean isDeviceConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;

        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return true;

    }

    @Override
    public void onListUpdated(List<CurrencyDb> currencyDbs) {
        mCurrencyList = currencyDbs;
    }
}
