package com.example.contacts.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contacts.ContactsApp;
import com.example.contacts.R;
import com.example.contacts.logic.ContactManager;
import com.example.contacts.logic.ContactValidationException;
import com.example.contacts.objects.Contact;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Form for entering name, email and phone.
 * Without EXTRA_CONTACT_ID it adds a new contact; with it, it updates that contact.
 */
public class ContactFormActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_ID = "com.example.contacts.CONTACT_ID";
    private static final long NO_ID = -1;

    private ContactManager contactManager;
    private long contactId = NO_ID;

    private TextInputLayout tilName, tilEmail, tilPhone;
    private TextInputEditText etName, etEmail, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        contactManager = ((ContactsApp) getApplication()).getContactManager();

        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        Button btnSave = findViewById(R.id.btn_save);

        contactId = getIntent().getLongExtra(EXTRA_CONTACT_ID, NO_ID);

        if (isUpdateMode()) {
            setTitle(R.string.title_update_contact);
            btnSave.setText(R.string.save_changes);
            // Only pre-fill on first creation; after rotation the views restore themselves.
            if (savedInstanceState == null) {
                Contact contact = contactManager.getContact(contactId);
                if (contact == null) {
                    Toast.makeText(this, R.string.contact_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                etName.setText(contact.getName());
                etEmail.setText(contact.getEmail());
                etPhone.setText(contact.getPhone());
            }
        } else {
            setTitle(R.string.title_add_contact);
        }

        btnSave.setOnClickListener(v -> save());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    private boolean isUpdateMode() {
        return contactId != NO_ID;
    }

    private void save() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);

        String name = textOf(etName);
        String email = textOf(etEmail);
        String phone = textOf(etPhone);

        // Check here too so the user gets feedback without waiting for the logic layer.
        if (name.trim().length() > 100) {
            tilName.setError("Name must be at most 100 characters");
            return;
        }
        if (!email.contains("@")) {
            tilEmail.setError("Email address is not valid");
            return;
        }
        int digits = phone.replaceAll("[^0-9]", "").length();
        if (digits < 7 || digits > 15) {
            tilPhone.setError("Phone must contain 7-15 digits");
            return;
        }

        try {
            if (isUpdateMode()) {
                if (!contactManager.updateContact(contactId, name, email, phone)) {
                    Toast.makeText(this, R.string.contact_not_found, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, R.string.contact_updated, Toast.LENGTH_SHORT).show();
            } else {
                contactManager.addContact(name, email, phone);
                Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (ContactValidationException e) {
            TextInputLayout target;
            switch (e.getField()) {
                case EMAIL: target = tilEmail; break;
                case PHONE: target = tilPhone; break;
                case NAME:
                default:    target = tilName; break;
            }
            target.setError(e.getMessage());
            target.requestFocus();
        }
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
