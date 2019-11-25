package com.example.localizer;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.here.sdk.mapviewlite.MapViewLite;

class TabAdapter extends FragmentPagerAdapter {

    public MapViewLite mapView;

    private Context myContext;
    int totalTabs;

    public TabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CarteFragment carteFragment = new CarteFragment();
                return carteFragment;
            case 1:
                ListeFragment listeFragment = new ListeFragment();
                return listeFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
