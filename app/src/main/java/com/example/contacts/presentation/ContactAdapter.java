package com.example.contacts.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.contacts.objects.Contact;

import java.util.ArrayList;
import java.util.List;

/** Two-line list rows: name on top, email and phone below. */
public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1,
                new ArrayList<Contact>());
    }

    public void setContacts(List<Contact> contacts) {
        setNotifyOnChange(false);
        clear();
        addAll(contacts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Contact contact = getItem(position);
        if (contact != null) {
            TextView line1 = view.findViewById(android.R.id.text1);
            TextView line2 = view.findViewById(android.R.id.text2);
            line1.setText(contact.getName());
            line2.setText(formatSubtitle(contact));
        }
        return view;
    }

    private String formatSubtitle(Contact contact) {
        String digits = contact.getPhone().replaceAll("[^0-9]", "");
        String phone;
        if (digits.length() == 10) {
            phone = "(" + digits.substring(0, 3) + ") " + digits.substring(3, 6) + "-" + digits.substring(6);
        } else if (digits.length() >= 7 && digits.length() <= 15) {
            phone = contact.getPhone();
        } else {
            phone = contact.getPhone() + " (?)";
        }
        String email = contact.getEmail();
        if (email.length() > 25) {
            email = email.substring(0, 22) + "...";
        }
        String initials = contact.getName().substring(0, 1).toUpperCase();
        if (contact.getName().contains(" ")) {
            initials += contact.getName().substring(contact.getName().lastIndexOf(' ') + 1, contact.getName().lastIndexOf(' ') + 2).toUpperCase();
        }
        return initials + "  |  " + email + "  •  " + phone;
    }
}
