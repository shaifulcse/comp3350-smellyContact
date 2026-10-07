package com.example.contacts.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Creates and upgrades the SQLite schema. */
public class ContactDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_CONTACTS = "contacts";
    static final String COL_ID = "_id";
    static final String COL_NAME = "name";
    static final String COL_EMAIL = "email";
    static final String COL_PHONE = "phone";

    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_CONTACTS + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_EMAIL + " TEXT NOT NULL, "
                    + COL_PHONE + " TEXT NOT NULL)";

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // FIXME: this deletes every user's contacts on upgrade. Write real migrations before v2!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }
}
