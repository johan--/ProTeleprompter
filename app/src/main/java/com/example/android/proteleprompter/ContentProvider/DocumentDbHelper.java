package com.example.android.proteleprompter.ContentProvider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.proteleprompter.ContentProvider.DocumentContract.DocumentEntry;

public class DocumentDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "files.db";

    private static final int DATABASE_VERSION = 8;


    public DocumentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_DOCUMENT_TABLE = "CREATE TABLE " + DocumentEntry.TABLE_NAME
                + "(" + DocumentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DocumentEntry.COLUMN_DOCUMENT_NAME + " TEXT NOT NULL," +
                DocumentEntry.COLUMN_DOCUMENT_TYPE + " TEXT NOT NULL," +
                DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME + " TEXT NOT NULL," +
                DocumentEntry.COLUMN_DOCUMENT_CONTENT + " TEXT," +
                DocumentEntry.COLUMN_DOCUMENT_URI + " TEXT"+ ");";

        db.execSQL(SQL_CREATE_DOCUMENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DocumentEntry.TABLE_NAME);
        onCreate(db);
    }
}
