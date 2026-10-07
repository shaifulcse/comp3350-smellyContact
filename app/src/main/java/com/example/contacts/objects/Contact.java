package com.example.contacts.objects;

/** Plain domain object shared by all three tiers. */
public class Contact {

    private long id;
    private final String name;
    private final String email;
    private final String phone;

    public Contact(long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /** Constructor for a contact not stored yet (no id). */
    public Contact(String name, String email, String phone) {
        this(0, name, email, phone);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    /** Returns a copy so callers can't mutate stored objects by accident. */
    public Contact copy() {
        return new Contact(id, name, email, phone);
    }

    @Override
    public String toString() {
        return name;
    }
}
