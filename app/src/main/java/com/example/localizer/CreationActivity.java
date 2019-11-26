package com.example.localizer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class CreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    public static final String PREFS_ACTION="Action";
    public static final String EXTRA_Image="Image";
    public static final String EXTRA_Titre="Titre";
    public static final String EXTRA_CoordN="CoordonnéesN";
    public static final String EXTRA_CoordO="CoordonnéesO";
    public static final String EXTRA_Contenu="Contenu";

    Intent intent;
    ImageView mImage;
    EditText mTitre, mContenu;
    Spinner mChoixImage;

    int image;
    double coordN, coordO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        mImage=findViewById(R.id.creationImage);
        mChoixImage = findViewById(R.id.Image_spinner);
        mTitre=findViewById(R.id.creationTitre);
        mContenu=findViewById(R.id.creationContenu);

        //on initialise le spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Image_spinner_resources, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChoixImage.setAdapter(adapter);
        mChoixImage.setOnItemSelectedListener(this);

        intent = getIntent();
        coordN = intent.getDoubleExtra(EXTRA_CoordN, -999);
        coordO = intent.getDoubleExtra(EXTRA_CoordO, -999);

        //On met une valeur par défaut pour éviter le carré blanc moche
        image = R.drawable.kakyoin;
        mImage.setImageResource(image);
    }

    public void CreerNote(View view) {
        Intent retour = new Intent();
        retour.putExtra(PREFS_ACTION, "creer");
        retour.putExtra(EXTRA_Image, image);
        retour.putExtra(EXTRA_Titre, mTitre.getText().toString());
        retour.putExtra(EXTRA_CoordN, coordN);
        retour.putExtra(EXTRA_CoordO, coordO);
        retour.putExtra(EXTRA_Contenu, mContenu.getText().toString());

        setResult(RESULT_OK, retour);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String nom = (String)parent.getItemAtPosition(position);

        switch(nom){
            case "Lieu": image = R.drawable.location;
                break;
            case "Monument": image = R.drawable.coliseum;
                break;
            case "Idée": image = R.drawable.speechbubble;
                break;
                default: image = R.drawable.kakyoin;
        }

        mImage.setImageResource(image);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
