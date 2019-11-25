package com.example.localizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.LoadSceneCallback;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.SceneError;


public class MainActivity extends AppCompatActivity implements ListeFragment.OnListFragmentInteractionListener{

    TabLayout tabLayout;
    ViewPagerSansSwipe viewPager;

    TabAdapter adapter;
    Note selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);

        adapter = new TabAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onListFragmentInteraction(Note item) {
        selectedNote = item;

        Intent i = new Intent(this, DetailsActivity.class);
        i.putExtra(DetailsActivity.EXTRA_Image, item.getImage());
        i.putExtra(DetailsActivity.EXTRA_Titre, item.getTitre());
        i.putExtra(DetailsActivity.EXTRA_CoordN, item.getCoordN());
        i.putExtra(DetailsActivity.EXTRA_CoordO, item.getCoordO());
        i.putExtra(DetailsActivity.EXTRA_Contenu, item.getContenu());

        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fm = getSupportFragmentManager();
        ListeFragment fragment = null;

        //moche mais j'ai pas trouvé autrement
        for(Fragment f: fm.getFragments()){
            String log = f.toString();

            if(log.substring(0,13).equals("ListeFragment"))
                fragment = (ListeFragment) f;
        }

        //On regarde l'action qu'on doit effectuer
        try{
            String action = data.getStringExtra(DetailsActivity.PREFS_ACTION);

            if(action.equals("supprimer")){
                fragment.supprimerNote(selectedNote);
                selectedNote = null;
            }

            if(action.equals("creer")){
                String titre = data.getStringExtra(CreationActivity.EXTRA_Titre);
                int image = data.getIntExtra(CreationActivity.EXTRA_Image, 0);
                double coordN = data.getDoubleExtra(CreationActivity.EXTRA_CoordN, 0);
                double coordO = data.getDoubleExtra(CreationActivity.EXTRA_CoordO, 0);
                String contenu = data.getStringExtra(CreationActivity.EXTRA_Contenu);

                fragment.creerNote(titre, contenu, image, coordN, coordO);
            }

        }catch (NullPointerException ignored){}
    }


}