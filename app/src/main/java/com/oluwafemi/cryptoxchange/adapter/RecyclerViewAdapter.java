package com.oluwafemi.cryptoxchange.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oluwafemi.cryptoxchange.R;
import com.oluwafemi.cryptoxchange.activity.ConverterActivity;
import com.oluwafemi.cryptoxchange.db.CurrencyDb;
import com.oluwafemi.cryptoxchange.model.CurrencyViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oluwafemi.cryptoxchange.utils.Constants.BTC_FRAG_TAG;
import static com.oluwafemi.cryptoxchange.utils.Constants.ETH_FRAG_TAG;

/**
 * Created by Oluwaphemmy on 19-Oct-17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CurrencyViewHolder> {

    private List<CurrencyDb> currencyList;
    private Context context;
    private CurrencyViewModel currencyViewModel;
    private String fragmentTag;
    private OnCardClickListener mListener;

    private String fragType = null;
//    private static String shareText;

    public RecyclerViewAdapter(List<CurrencyDb> currencyList, Context context,
                               CurrencyViewModel currencyViewModel, String tag, OnCardClickListener listener) {
        this.currencyList = currencyList;
        this.context = context;
        this.currencyViewModel = currencyViewModel;
        this.fragmentTag = tag;
        this.mListener = listener;
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_card, parent, false);
        return new CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CurrencyViewHolder holder, final int position) {

        String currencySymbol = currencyList.get(position).getCurrencySymbol();
        final String currCode = currencyList.get(position).getCurrencyCode();

        holder.currencyCode.setText(currCode);
        holder.currencyName.setText(currencyList.get(position).getCurrencyName());
        holder.updatedDate.setText(formatDate(currencyList.get(position).getLastUpdatedDate()));


        if (this.fragmentTag.equalsIgnoreCase(BTC_FRAG_TAG)) {
            holder.rateText.setText(R.string.btc_sym_1);
            String btcRateText = currencySymbol + " " + currencyList.get(position).getBtcCurrencyRate();
            holder.currencyRate.setText(btcRateText);
            holder.shareText = "1 btc = " + " " + currencyList.get(position).getBtcCurrencyRate() + " " + currCode;
            fragType = BTC_FRAG_TAG;
        }
        else if (this.fragmentTag.equalsIgnoreCase(ETH_FRAG_TAG)) {
            holder.rateText.setText(R.string.eth_sym);
            String ethRateText = currencySymbol + " " + currencyList.get(position).getEthCurrencyRate();
            holder.currencyRate.setText(ethRateText);
            holder.shareText = "1 eth = " + " " + currencyList.get(position).getEthCurrencyRate() + " " + currCode;
            fragType = ETH_FRAG_TAG;

        }

        holder.clearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyViewModel.deleteCurrency(currencyList.get(holder.getAdapterPosition()));
                Toast.makeText(context, currencyList.get(holder.getAdapterPosition()).getCurrencyName() +
                        " successfully deleted", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCardClick(currCode, fragType);
            }
        });

        holder.shareRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMyRate(holder.shareText);
            }
        });

    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }


    class CurrencyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvSymbol)
        TextView currencyCode;
        @BindView(R.id.tvCountryName)
        TextView currencyName;
        @BindView(R.id.tvBtc)
        TextView rateText;
        @BindView(R.id.tvRate)
        TextView currencyRate;
        @BindView(R.id.tvDate)
        TextView updatedDate;
        @BindView(R.id.ivClear)
        ImageView clearIcon;
        @BindView(R.id.llClickLayout)
        LinearLayout cCard;
        @BindView(R.id.tvShare)
        TextView shareRate;
        String shareText;

        CurrencyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void shareMyRate(String rate) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String text = "CryptoXchange latest exchange rate \n\n " + rate;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Rate");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, "Select how you want to share"));
    }

    // method to format date
    private String formatDate (Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        return sdf.format(date);
    }

    // interface to handle click
    public interface OnCardClickListener {
        void onCardClick(String currCode, String fragType);
    }
}
