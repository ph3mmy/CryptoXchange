package com.oluwafemi.cryptoxchange.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;

import com.oluwafemi.cryptoxchange.activity.MainActivity;

/**
 * Created by Oluwaphemmy on 22-Oct-17.
 */

public class FabVisibilityListener extends RecyclerView.OnScrollListener {

    private FloatingActionButton fab;

    public FabVisibilityListener(Activity context) {
//        Activity activity = context;
        fab = ((MainActivity)context).getFab();
    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (recyclerView.getAdapter().getItemCount() < 4 && !fab.isShown()) {
            fab.show();
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy < -5 && !fab.isShown()) {
            fab.show();
        } else if(dy > 5 && fab.isShown()){
            fab.hide();
        } else if(dy == 1 && !fab.isShown()){
            fab.show();
        }
    }
}
