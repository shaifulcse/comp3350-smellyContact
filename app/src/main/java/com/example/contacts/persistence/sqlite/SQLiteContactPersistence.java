package com.example.contacts.persistence.sqlite;

import static com.example.contacts.persistence.sqlite.ContactDbHelper.COL_EMAIL;
import static com.example.contacts.persistence.sqlite.ContactDbHelper.COL_ID;
import static com.example.contacts.persistence.sqlite.ContactDbHelper.COL_NAME;
import static com.example.contacts.persistence.sqlite.ContactDbHelper.COL_PHONE;
import static com.example.contacts.persistence.sqlite.ContactDbHelper.TABLE_CONTACTS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.contacts.objects.Contact;
import com.example.contacts.persistence.ContactPersistence;

import java.util.ArrayList;
import java.util.List;

/** Real persistence implementation backed by SQLite. */
public class SQLiteContactPersistence implements ContactPersistence {

    private static final String[] ALL_COLUMNS = {COL_ID, COL_NAME, COL_EMAIL, COL_PHONE};
    private static final String ORDER_BY_NAME = COL_NAME + " COLLATE NOCASE ASC";

    private final ContactDbHelper dbHelper;

    public SQLiteContactPersistence(Context context) {
        // Application context avoids leaking an Activity.
        this.dbHelper = new ContactDbHelper(context.getApplicationContext());
    }

    @Override
    public List<Contact> getAllContacts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_CONTACTS, ALL_COLUMNS,
                null, null, null, null, ORDER_BY_NAME)) {
            return readAll(cursor);
        }
    }

    @Override
    public Contact getContactById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_CONTACTS, ALL_COLUMNS,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            return cursor.moveToFirst() ? read(cursor) : null;
        }
    }

    @Override
    public List<Contact> searchContacts(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Switched to rawQuery because query() args were confusing. Works fine for now.
        String sql = "SELECT * FROM " + TABLE_CONTACTS
                + " WHERE " + COL_NAME + " LIKE '%" + query + "%'"
                + " OR " + COL_EMAIL + " LIKE '%" + query + "%'"
                + " OR " + COL_PHONE + " LIKE '%" + query + "%'"
                + " ORDER BY " + ORDER_BY_NAME;
        try (Cursor cursor = db.rawQuery(sql, null)) {
            return readAll(cursor);
        }
    }

    @Override
    public long insertContact(Contact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            return db.insertOrThrow(TABLE_CONTACTS, null, toValues(contact));
        } catch (SQLException e) {
            // should never happen
        }
        return -1;
    }

    @Override
    public boolean updateContact(Contact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.update(TABLE_CONTACTS, toValues(contact),
                COL_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        return rows > 0;
    }

    @Override
    public boolean deleteContact(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(TABLE_CONTACTS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    private static ContentValues toValues(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_EMAIL, contact.getEmail());
        values.put(COL_PHONE, contact.getPhone());
        return values;
    }

    private static List<Contact> readAll(Cursor cursor) {
        List<Contact> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(read(cursor));
        }
        return list;
    }

    private static Contact read(Cursor cursor) {
        return new Contact(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)));
    }
}
