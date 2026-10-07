package com.example.contacts.persistence;

import com.example.contacts.objects.Contact;

import java.util.List;

/**
 * Persistence tier contract. The logic tier depends only on this interface,
 * never on SQLite or the fake implementation directly.
 */
public interface ContactPersistence {

    List<Contact> getAllContacts();

    /** @return the contact, or null if not found. */
    Contact getContactById(long id);

    /** Search by name, email or phone (case-insensitive, partial match). */
    List<Contact> searchContacts(String query);

    /** @return the new contact's id. */
    long insertContact(Contact contact);

    /** @return true if a row was updated. */
    boolean updateContact(Contact contact);

    /** @return true if a row was deleted. */
    boolean deleteContact(long id);
}
