package com.example.baselistview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Base extends SQLiteOpenHelper {
    public Base(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //Ejecutamos este método cuando deseamos crear nuestra BD
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS Compras(Id INTEGER PRIMARY KEY AUTOINCREMENT, Nombre text);";
        db.execSQL(query);
    }

    //Ejecutamos este método cuando deseamos actualizar la estructura de la BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query;
        query = "DROP TABLE IF EXISTS Compras";
        db.execSQL(query);
        onCreate(db);
    }
}
