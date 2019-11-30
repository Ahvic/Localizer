package com.example.localizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    //Ce qu'on reçoit avec l'intent
    public static final String PREFS_ACTION="Action";
    public static final String EXTRA_Image="Image";
    public static final String EXTRA_Titre="Titre";
    public static final String EXTRA_CoordN="CoordonnéesN";
    public static final String EXTRA_CoordO="CoordonnéesO";
    public static final String EXTRA_Contenu="Contenu";

    Intent intent;
    ImageView mImage;
    TextView mTitre, mContenu, mCoordonnée;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        intent = getIntent();
        mImage = findViewById(R.id.detailsImage);
        mTitre = findViewById(R.id.detailsTitre);
        mCoordonnée = findViewById(R.id.detailsCoord);
        mContenu = findViewById(R.id.detailsContenu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mImage.setImageResource(intent.getIntExtra(EXTRA_Image, -1));
        mTitre.setText(intent.getStringExtra(EXTRA_Titre));
        mContenu.setText(intent.getStringExtra(EXTRA_Contenu));

        mCoordonnée.setText(
                "N: " + intent.getDoubleExtra((EXTRA_CoordN), 0) +
                " O: " + intent.getDoubleExtra(EXTRA_CoordO, 0)
        );
    }

    public void supprimerNote(View view){
        Intent retour = new Intent();
        retour.putExtra(PREFS_ACTION, "supprimer");
        retour.putExtra(EXTRA_Titre, intent.getStringExtra(EXTRA_Titre));

        setResult(RESULT_OK, retour);
        finish();
    }
}
