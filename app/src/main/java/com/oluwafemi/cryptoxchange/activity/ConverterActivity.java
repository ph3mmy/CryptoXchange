package com.oluwafemi.cryptoxchange.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oluwafemi.cryptoxchange.R;
import com.oluwafemi.cryptoxchange.db.CurrencyDb;
import com.oluwafemi.cryptoxchange.model.CurrencyViewModel;
import com.oluwafemi.cryptoxchange.model.GetSingleCurrencyListener;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oluwafemi.cryptoxchange.utils.Constants.BTC_FRAG_TAG;
import static com.oluwafemi.cryptoxchange.utils.Constants.ETH_FRAG_TAG;

/**
 * Created by Oluwaphemmy on 23-Oct-17.
 */

public class ConverterActivity extends AppCompatActivity implements GetSingleCurrencyListener{

    private static final String TAG = "ConverterActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvTitleText)
    TextView title;
    @BindView(R.id.tvConText)
    TextView conversionTv;
    @BindView(R.id.etBtcVal)
    EditText etBtc;
    @BindView(R.id.etOtherVal)
    EditText etOther;
    @BindView(R.id.til_btc_con)
    TextInputLayout tilBtcValue;
    @BindView(R.id.til_other_con)
    TextInputLayout tilOtherValue;
    String btcEthTitle, currencyFullName;
    TextWatcher etBtcWatcher, etOtherWatcher;
    boolean btcFrag;

    CurrencyViewModel currencyViewModel;
    CurrencyDb currency;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.converter_title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currencyViewModel = ViewModelProviders.of(this).get(CurrencyViewModel.class);

        // retrieving passed intents
        Bundle mBundle = getIntent().getExtras();
        String currencyCode = null;
        if (mBundle != null) {
            currencyCode = mBundle.getString("currencyCode");
        String fragType = mBundle.getString("fragType");

        if(BTC_FRAG_TAG.equalsIgnoreCase(fragType)) {
            btcEthTitle = "BTC / ";
            currencyFullName = "Bitcoin";
            btcFrag = true;
            tilBtcValue.setHint("BTC Value ");
        }
        else if(ETH_FRAG_TAG.equalsIgnoreCase(fragType)) {
            btcEthTitle = "ETH / ";
            currencyFullName = "Ethereum";
            btcFrag = false;
            tilBtcValue.setHint("ETH Value");
        }
    }
        tilOtherValue.setHint(currencyCode + " value" );

        currencyViewModel.getCurrencyByCode(currencyCode, this);

        String titleText = btcEthTitle + " " + currencyCode + " Converter";
        title.setText(titleText);
        etOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOther.setFocusable(true);
            }
        });

    }

    @Override
    public void onCurrencyLoaded(CurrencyDb currencyDb) {
        currency = currencyDb;
        String curName = currencyFullName.toUpperCase();
        String otherCurName = currency.getCurrencyName().toUpperCase();
        String subtitleString = "Easily convert your values \nbetween\n" +curName + " & " + otherCurName;
        conversionTv.setText(subtitleString);

        etOtherWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s)) {
                    Float enteredVal = Float.parseFloat(s.toString());
                    Double result;
                    if (btcFrag) {
                        result = (currency.getInvBtcrate() * enteredVal);
                    } else {
                        result = (currency.getInvEthRate() * enteredVal);
                    }
                    etBtc.removeTextChangedListener(etBtcWatcher);
                    String formattedResult = String.valueOf(formatCurrencyDp(result));
                    etBtc.setText(formattedResult);
                } else {
                    etBtc.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        etBtcWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    Double enteredVal = Double.parseDouble(s.toString());
                    Double result;
                    if (btcFrag) {
                        result = (currency.getBtcCurrencyRate() * enteredVal);
                    } else {
                        result = (currency.getEthCurrencyRate() * enteredVal);
                    }
                    etOther.removeTextChangedListener(etOtherWatcher);
                    String formattedResult = String.valueOf(formatCurrencyDp(result));
                    etOther.setText(formattedResult);
                } else {
                    etOther.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etBtc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etBtc.setText("");
                    etOther.removeTextChangedListener(etOtherWatcher);
                    etBtc.addTextChangedListener(etBtcWatcher);
                }
            }
        });

        etOther.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etOther.setText("");
                    etBtc.removeTextChangedListener(etBtcWatcher);
                    etOther.addTextChangedListener(etOtherWatcher);
                }
            }
        });
    }


    public static double formatCurrencyDp (Double rate){
        DecimalFormat threeDp = new DecimalFormat("#.000");
        return Double.parseDouble(threeDp.format(rate));
    }
}
