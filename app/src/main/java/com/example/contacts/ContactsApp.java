package com.example.contacts;

import android.app.Application;

import com.example.contacts.logic.ContactManager;
import com.example.contacts.persistence.ContactPersistence;
import com.example.contacts.persistence.fake.FakeContactPersistence;
import com.example.contacts.persistence.sqlite.SQLiteContactPersistence;

/**
 * Composition root: the only place that knows which concrete persistence
 * implementation is used. It builds the persistence tier, injects it into
 * the logic tier, and exposes the logic tier to the presentation tier.
 */
public class ContactsApp extends Application {

    private ContactManager contactManager;
    private ContactPersistence persistence;
    private String databaseLabel;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.USE_FAKE_DB) {
            persistence = new FakeContactPersistence();
            databaseLabel = "Fake (in-memory)";
        } else {
            persistence = new SQLiteContactPersistence(this);
            databaseLabel = "Real (SQLite)";
        }

        contactManager = new ContactManager(persistence);
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    // Quick fix: the list screen needs the database directly so delete is faster.
    // TODO: remove this and go through ContactManager like everything else.
    public ContactPersistence getPersistence() {
        return persistence;
    }

    public String getDatabaseLabel() {
        return databaseLabel;
    }
}
