package com.example.localizer;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.here.sdk.mapviewlite.MapViewLite;

class TabAdapter extends FragmentPagerAdapter {

    int totalTabs;

    public TabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    /**
     * Sert a renvoyer le fragment correspondant quand on change d'onglet
     *
     * @param position
     * @return
     */

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

    @Override
    public int getCount() {
        return totalTabs;
    }
}
