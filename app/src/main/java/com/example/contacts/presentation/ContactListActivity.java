package com.example.contacts.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contacts.ContactsApp;
import com.example.contacts.R;
import com.example.contacts.logic.ContactManager;
import com.example.contacts.objects.Contact;
import com.example.contacts.persistence.ContactPersistence;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * One list screen reused for four actions:
 * show all, delete (tap to delete), update (tap to edit) and search.
 */
public class ContactListActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "com.example.contacts.MODE";
    public static final String MODE_VIEW = "view";
    public static final String MODE_DELETE = "delete";
    public static final String MODE_UPDATE = "update";
    public static final String MODE_SEARCH = "search";

    private ContactManager contactManager;
    private ContactAdapter adapter;
    private String mode;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        contactManager = ((ContactsApp) getApplication()).getContactManager();

        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = MODE_VIEW;

        TextView tvInstructions = findViewById(R.id.tv_instructions);
        View searchContainer = findViewById(R.id.til_search);
        etSearch = findViewById(R.id.et_search);

        switch (mode) {
            case MODE_DELETE:
                setTitle(R.string.title_delete_contact);
                tvInstructions.setText(R.string.instructions_delete);
                break;
            case MODE_UPDATE:
                setTitle(R.string.title_update_contact);
                tvInstructions.setText(R.string.instructions_update);
                break;
            case MODE_SEARCH:
                setTitle(R.string.title_search_contacts);
                tvInstructions.setText(R.string.instructions_search);
                searchContainer.setVisibility(View.VISIBLE);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
                    @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
                    @Override public void afterTextChanged(Editable s) { refresh(); }
                });
                break;
            case MODE_VIEW:
            default:
                setTitle(R.string.title_all_contacts);
                tvInstructions.setText(R.string.instructions_view);
                break;
        }

        ListView listView = findViewById(R.id.lv_contacts);
        listView.setEmptyView(findViewById(R.id.tv_empty));
        adapter = new ContactAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) ->
                onContactClicked(adapter.getItem(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(); // picks up changes made in ContactFormActivity
    }

    private void refresh() {
        String query = MODE_SEARCH.equals(mode) ? etSearch.getText().toString() : "";
        adapter.setContacts(contactManager.searchContacts(query));
    }

    private void onContactClicked(Contact contact) {
        if (contact == null) return;
        switch (mode) {
            case MODE_DELETE:
                confirmDelete(contact);
                break;
            case MODE_UPDATE:
                Intent intent = new Intent(this, ContactFormActivity.class);
                intent.putExtra(ContactFormActivity.EXTRA_CONTACT_ID, contact.getId());
                startActivity(intent);
                break;
            default:
                showDetails(contact);
                break;
        }
    }

    private void confirmDelete(Contact contact) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_delete_contact)
                .setMessage(getString(R.string.confirm_delete, contact.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    String deletedName = ((ContactsApp) getApplication()).getPersistence()
                            .getContactById(contact.getId()).getName();
                    ContactPersistence db = ((ContactsApp) getApplication()).getPersistence();
                    boolean deleted = db.deleteContact(contact.getId());
                    if (deleted) {
                        Toast.makeText(this, deletedName + " deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.contact_not_found, Toast.LENGTH_SHORT).show();
                    }
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showDetails(Contact contact) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(contact.getName())
                .setMessage(getString(R.string.contact_details,
                        contact.getEmail(), contact.getPhone()))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
