package com.example.contacts.persistence.fake;

import com.example.contacts.objects.Contact;
import com.example.contacts.persistence.ContactPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * In-memory "database" for development and unit tests.
 * Pure Java (no Android classes), so it runs on the JVM.
 * Data is lost when the app process dies.
 */
public class FakeContactPersistence implements ContactPersistence {

    private final List<Contact> contacts = new ArrayList<>();
    private long nextId = 1;

    public FakeContactPersistence() {
        this(true);
    }

    public FakeContactPersistence(boolean seedSampleData) {
        if (seedSampleData) {
            insertContact(new Contact("Alice Martin", "alice@example.com", "204-555-0101"));
            insertContact(new Contact("Bob Singh", "bob@example.com", "204-555-0102"));
            insertContact(new Contact("Chloé Tremblay", "chloe@example.com", "204-555-0103"));
        }
    }

    @Override
    public synchronized List<Contact> getAllContacts() {
        List<Contact> result = new ArrayList<>();
        for (Contact c : contacts) {
            result.add(c.copy());
        }
        sortByName(result);
        return result;
    }

    @Override
    public synchronized Contact getContactById(long id) {
        int index = indexOf(id);
        return index >= 0 ? contacts.get(index).copy() : null;
    }

    @Override
    public synchronized List<Contact> searchContacts(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        // Search phone by digits only, so "2045550101" finds "204-555-0101".
        String qDigits = q.replaceAll("[^0-9]", "");
        List<Contact> result = new ArrayList<>();
        for (Contact c : contacts) {
            String phoneDigits = c.getPhone().replaceAll("[^0-9]", "");
            if (c.getName().toLowerCase(Locale.ROOT).contains(q)
                    || c.getEmail().toLowerCase(Locale.ROOT).contains(q)
                    || c.getPhone().contains(q)
                    || (qDigits.length() >= 7 && phoneDigits.contains(qDigits))) {
                result.add(c.copy());
            }
        }
        Collections.sort(result, new Comparator<Contact>() {
            @Override
            public int compare(Contact a, Contact b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        return result;
    }

    @Override
    public synchronized long insertContact(Contact contact) {
        long id = nextId++;
        contacts.add(new Contact(id, contact.getName(), contact.getEmail(), contact.getPhone()));
        return id;
    }

    @Override
    public synchronized boolean updateContact(Contact contact) {
        int index = indexOf(contact.getId());
        if (index < 0) return false;
        contacts.set(index, contact.copy());
        return true;
    }

    @Override
    public synchronized boolean deleteContact(long id) {
        int index = indexOf(id);
        if (index < 0) return false;
        contacts.remove(index);
        return true;
    }

    private int indexOf(long id) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == id) return i;
        }
        return -1;
    }

    private static void sortByName(List<Contact> list) {
        Collections.sort(list, new Comparator<Contact>() {
            @Override
            public int compare(Contact a, Contact b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }
}
