package com.example.contacts.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contacts.ContactsApp;
import com.example.contacts.R;

/** Home screen with the five main actions. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContactsApp app = (ContactsApp) getApplication();
        TextView tvDatabase = findViewById(R.id.tv_database);
        tvDatabase.setText(getString(R.string.database_in_use, app.getDatabaseLabel()));

        findViewById(R.id.btn_add).setOnClickListener(v ->
                startActivity(new Intent(this, ContactFormActivity.class)));
        findViewById(R.id.btn_delete).setOnClickListener(v ->
                openList(ContactListActivity.MODE_DELETE));
        findViewById(R.id.btn_update).setOnClickListener(v ->
                openList(ContactListActivity.MODE_UPDATE));
        findViewById(R.id.btn_show_all).setOnClickListener(v ->
                openList(ContactListActivity.MODE_VIEW));
        findViewById(R.id.btn_search).setOnClickListener(v ->
                openList(ContactListActivity.MODE_SEARCH));
    }

    private void openList(String mode) {
        Intent intent = new Intent(this, ContactListActivity.class);
        intent.putExtra(ContactListActivity.EXTRA_MODE, mode);
        startActivity(intent);
    }
}
