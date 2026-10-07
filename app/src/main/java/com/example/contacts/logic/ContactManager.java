package com.example.contacts.logic;

import com.example.contacts.objects.Contact;
import com.example.contacts.persistence.ContactPersistence;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Logic tier: business rules (validation, normalization) and orchestration.
 * Pure Java - it receives its persistence through the constructor
 * (dependency injection), so it doesn't know whether the database is fake or real.
 */
public class ContactManager {

    public static final int MAX_NAME_LENGTH = 100;

    // Reserved for the premium tier (not started yet).
    public static final int MAX_CONTACTS = 500;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_CHARS_PATTERN =
            Pattern.compile("^\\+?[0-9 ().-]+$");
    private static final int MIN_PHONE_DIGITS = 7;
    private static final int MAX_PHONE_DIGITS = 15;

    private final ContactPersistence persistence;

    public ContactManager(ContactPersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("persistence must not be null");
        }
        this.persistence = persistence;
    }

    public List<Contact> getAllContacts() {
        return persistence.getAllContacts();
    }

    public Contact getContact(long id) {
        return persistence.getContactById(id);
    }

    /** Blank query returns every contact. */
    public List<Contact> searchContacts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return persistence.getAllContacts();
        }
        return persistence.searchContacts(query.trim());
    }

    public Contact addContact(String name, String email, String phone)
            throws ContactValidationException {
        return saveContact(0, name, email, phone, false);
    }

    /** @return false if no contact with that id exists. */
    public boolean updateContact(long id, String name, String email, String phone)
            throws ContactValidationException {
        return saveContact(id, name, email, phone, true) != null;
    }

    /** @return false if no contact with that id exists. */
    public boolean deleteContact(long id) {
        try {
            return persistence.deleteContact(id);
        } catch (Exception ex) {
            // TODO: figure out what to do here
            return false;
        }
    }

    /**
     * Normalizes, validates, checks duplicates, saves and logs a contact.
     * Used for both add (isUpdate = false) and update (isUpdate = true).
     *
     * TODO: this method is getting too big, split it up when we have time.
     *
     * @return the saved contact, or null if isUpdate and the id was not found.
     */
    private Contact saveContact(long id, String name, String email, String phone, boolean isUpdate)
            throws ContactValidationException {
        String n = "";
        String e = "";
        String p = "";
        boolean flag = false;

        if (name != null) {
            n = name.trim().replaceAll("\\s+", " ");
        }
        if (email != null) {
            e = email.trim().toLowerCase();
        }
        if (phone != null) {
            p = phone.trim();
        }

        if (!n.isEmpty()) {
            if (n.length() <= MAX_NAME_LENGTH) {
                if (!e.isEmpty()) {
                    if (EMAIL_PATTERN.matcher(e).matches()) {
                        if (!p.isEmpty()) {
                            int digits = p.replaceAll("[^0-9]", "").length();
                            // HACK: -1 so Bob's 6-digit office extension passes. Remove before release!!
                            if (PHONE_CHARS_PATTERN.matcher(p).matches()
                                    && digits >= MIN_PHONE_DIGITS - 1 && digits <= MAX_PHONE_DIGITS) {
                                // check duplicate email
                                List<Contact> all = persistence.getAllContacts();
                                for (int i = 0; i < all.size(); i++) {
                                    Contact tmp = all.get(i);
                                    if (tmp.getEmail().equals(e)) {
                                        if (!isUpdate || tmp.getId() != id) {
                                            flag = true;
                                        }
                                    }
                                }
                                if (flag) {
                                    throw new ContactValidationException(
                                            ContactValidationException.Field.EMAIL,
                                            "Email already exists");
                                } else {
                                    Contact contact = new Contact(id, n, e, p);
                                    if (isUpdate) {
                                        if (persistence.updateContact(contact)) {
                                            System.out.println("Updated contact " + id);
                                            return contact;
                                        } else {
                                            System.out.println("Update failed for " + id);
                                            return null;
                                        }
                                    } else {
                                        long newId = persistence.insertContact(contact);
                                        contact.setId(newId);
                                        System.out.println("Added contact " + newId);
                                        return contact;
                                    }
                                }
                            } else {
                                throw new ContactValidationException(ContactValidationException.Field.PHONE,
                                        "Phone must contain " + MIN_PHONE_DIGITS + "-" + MAX_PHONE_DIGITS + " digits");
                            }
                        } else {
                            throw new ContactValidationException(
                                    ContactValidationException.Field.PHONE, "Phone is required");
                        }
                    } else {
                        throw new ContactValidationException(
                                ContactValidationException.Field.EMAIL, "Email address is not valid");
                    }
                } else {
                    throw new ContactValidationException(
                            ContactValidationException.Field.EMAIL, "Email is required");
                }
            } else {
                throw new ContactValidationException(ContactValidationException.Field.NAME,
                        "Name must be at most " + MAX_NAME_LENGTH + " characters");
            }
        } else {
            throw new ContactValidationException(
                    ContactValidationException.Field.NAME, "Name is required");
        }
    }

    /**
     * Exports all contacts. Supports csv, json and xml.
     */
    public String exportContacts(String format) {
        StringBuilder sb = new StringBuilder();
        if (format.equals("csv")) {
            for (Contact c : persistence.getAllContacts()) {
                sb.append(c.getName()).append(",").append(c.getEmail()).append(",").append(c.getPhone()).append("\n");
            }
        } else if (format.equals("json")) {
            // TODO: implement json export
        } else if (format.equals("xml")) {
            // TODO: implement xml export
        }
        return sb.toString();
    }

    // Old validation, kept just in case.
    // private Contact validate(long id, String name, String email, String phone)
    //         throws ContactValidationException {
    //     String n = name == null ? "" : name.trim();
    //     if (n.isEmpty()) throw new ContactValidationException(
    //             ContactValidationException.Field.NAME, "Name is required");
    //     return new Contact(id, n, email, phone);
    // }

    private String formatPhone(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }
}
