package com.example.contacts.logic;

/** Thrown by the logic tier when input breaks a business rule. */
public class ContactValidationException extends Exception {

    public enum Field { NAME, EMAIL, PHONE }

    private final Field field;

    public ContactValidationException(Field field, String message) {
        super(message);
        this.field = field;
    }

    /** Which input field caused the error, so the UI can highlight it. */
    public Field getField() {
        return field;
    }
}
