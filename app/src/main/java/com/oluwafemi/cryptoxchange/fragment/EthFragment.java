package com.oluwafemi.cryptoxchange.fragment;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.oluwafemi.cryptoxchange.R;
import com.oluwafemi.cryptoxchange.activity.ConverterActivity;
import com.oluwafemi.cryptoxchange.activity.MainActivity;
import com.oluwafemi.cryptoxchange.adapter.RecyclerViewAdapter;
import com.oluwafemi.cryptoxchange.db.CurrencyDb;
import com.oluwafemi.cryptoxchange.model.CurrencyViewModel;
import com.oluwafemi.cryptoxchange.utils.FabVisibilityListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Oluwaphemmy on 09-Oct-17.
 */

public class EthFragment extends LifecycleFragment implements RecyclerViewAdapter.OnCardClickListener {

    private static final String TAG = "EthFragment";
    @BindView(R.id.ethRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rlEmptyState)
    RelativeLayout emptyState;

    LinearLayoutManager linearLayoutManager;
    RecyclerViewAdapter adapter;
    CurrencyViewModel currencyViewModel;
    FabVisibilityListener fabVisibilityListener;

    public EthFragment() {
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_eth, container, false);
        ButterKnife.bind(this, view);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        fabVisibilityListener = new FabVisibilityListener(getActivity());
        currencyViewModel = ViewModelProviders.of(this).get(CurrencyViewModel.class);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)getActivity()).updateCurrencyRates(refreshLayout);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currencyViewModel.getAllCurrencyList().observe(this, new Observer<List<CurrencyDb>>() {
            @Override
            public void onChanged(@Nullable List<CurrencyDb> currencyDbs) {
                adapter = new RecyclerViewAdapter(currencyDbs, getActivity(), currencyViewModel, TAG, EthFragment.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() <= 0) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }

                recyclerView.addOnScrollListener(fabVisibilityListener);
            }
        });

    }

    @Override
    public void onCardClick(String currCode, String fragType) {
        Intent converterIntent = new Intent(getActivity(), ConverterActivity.class);
        converterIntent.putExtra("currencyCode", currCode);
        converterIntent.putExtra("fragType", fragType);
        getActivity().startActivity(converterIntent);

    }
}
