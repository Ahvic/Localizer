package com.example.localizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper{

    public static final String TABLE ="notes";

    public static final String COLUMN_ID="id";
    public static final String COLUMN_TITRE="titre";
    public static final String COLUMN_CONTENU="contenu";
    public static final String COLUMN_IMAGE="image";
    public static final String COLUMN_COORDN="coordn";
    public static final String COLUMN_COORDO="coordo";

    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String requete = "create table "+ TABLE +" ( " +
                COLUMN_ID + " integer primary key , " +
                COLUMN_TITRE + " text not null ," +
                COLUMN_CONTENU + " text not null ," +
                COLUMN_IMAGE + " integer not null ," +
                COLUMN_COORDN + " double not null ," +
                COLUMN_COORDO + " double not null" +
                ") ;";

        db.execSQL(requete);
    }

    /**
     * Utilisé si on doit mettre à jour la BDD
     * Restera toujours vide en théorie
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
