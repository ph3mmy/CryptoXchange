package com.oluwafemi.cryptoxchange.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.oluwafemi.cryptoxchange.fragment.BtcFragment;
import com.oluwafemi.cryptoxchange.fragment.EthFragment;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Oluwaphemmy on 09-Oct-17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> tabFragmentList = new ArrayList<>();
    private ArrayList<String> tabFragmentTitleList = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
return tabFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        tabFragmentList.add(fragment);
        tabFragmentTitleList.add(title);
    }


    @Override
    public int getCount() {
        return tabFragmentList.size();
    }
}
