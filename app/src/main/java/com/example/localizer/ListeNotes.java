package com.example.localizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class ListeNotes {

    private ArrayList<Note> listeNote;
    private Context appContext;
    private DBOpenHelper dbHelper;

    public ListeNotes(Context ctx)
    {
        listeNote = new ArrayList<>();
        appContext = ctx.getApplicationContext();

        //TODO: supprimer
        appContext.deleteDatabase("DBnotes");

        dbHelper = new DBOpenHelper(appContext, "DBnotes", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DBOpenHelper.TABLE, new String[]{
                DBOpenHelper.COLUMN_TITRE,
                DBOpenHelper.COLUMN_CONTENU,
                DBOpenHelper.COLUMN_IMAGE,
                DBOpenHelper.COLUMN_COORDN,
                DBOpenHelper.COLUMN_COORDO}, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                listeNote.add(new Note(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4)));
            }
        }
        finally {
            cursor.close();
        }

        db.close();

        if(listeNote.size() < 2){
            creerNote("Giorno best jojo", "WOHO WOOOOO", R.drawable.giorno, 0, 0);
            creerNote("IGGY", "DED DED DED", R.drawable.kakyoin, 0, 0);
        }

    }

    /**
     * Renvoi la note i
     *
     * @param i
     * @return
     */

    public Note get(int i)
    {
        if(i < 0 || i > listeNote.size()) return null;

        return listeNote.get(i);
    }

    public int getNbNote()
    {
        return listeNote.size();
    }

    /**
     * Ajout d'une nouvelle note dans la BDD et dans la liste
     *
     * @param titre
     * @param contenu
     */

    public int creerNote(String titre, String contenu, int image, double coordN, double coordO)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues toAdd = new ContentValues();

        Log.e("ListeNotes","Latitude: " + coordN + " longitude: " + coordO);


        if(titre.isEmpty()) return -1;

        toAdd.put(DBOpenHelper.COLUMN_TITRE, titre);
        toAdd.put(DBOpenHelper.COLUMN_CONTENU, contenu);
        toAdd.put(DBOpenHelper.COLUMN_IMAGE, image);
        toAdd.put(DBOpenHelper.COLUMN_COORDN, coordN);
        toAdd.put(DBOpenHelper.COLUMN_COORDO, coordO);

        db.insert(DBOpenHelper.TABLE, null, toAdd);
        db.close();

        listeNote.add(new Note(titre, contenu, image, coordN, coordO));

        return listeNote.size() - 1;
    }

    /**
     * On efface la note de la BDD et de la liste
     * par rapport à son titre, pas idéal mais suffisant
     *
     * @param titre
     * @return
     */

    public int effacerNote(String titre)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBOpenHelper.TABLE, DBOpenHelper.COLUMN_TITRE + " = ?", new String[]{titre});
        db.close();

        for(int i = 0; i < listeNote.size(); i++)
        {
            Note n = listeNote.get(i);

            if(n.getTitre() == titre)
            {
                listeNote.remove(i);
                return i;
            }
        }

        return -1;
    }

    public int size() {
        return listeNote.size();
    }
}
