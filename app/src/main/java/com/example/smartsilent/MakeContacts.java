package com.example.smartsilent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MakeContacts extends AppCompatActivity {
    Button btnview, listview;
    TextView txtname,txtphno;
    static final int PICK_CONTACT = 1;
    String st;
    final private int REQUEST_MULTIPLE_PERMISSIONS = 124;

    ArrayList<String> contacts_name = new ArrayList<>();
    ArrayList<String> contacts_number = new ArrayList<>();

@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.make_contacts);
    AccessContact();
    btnview = (Button) findViewById(R.id.btnload);
    listview = (Button) findViewById(R.id.lstload);
    txtname = (TextView) findViewById(R.id.txtname);
    txtphno = (TextView) findViewById(R.id.txtphno);


    btnview.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }

    });

    listview.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MakeContacts.this, SelectedContacts.class);

            Bundle b = new Bundle();
            b.putStringArrayList("contacts_name", contacts_name);
            b.putStringArrayList("contacts_number", contacts_number);

            intent.putExtras(b);
            startActivity(intent);
        }

    });

}


    private void AccessContact()
    {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_MULTIPLE_PERMISSIONS);
            return;
        }
    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);

            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MakeContacts.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        try {
                            String cNumber = "N/A";
                            if (hasPhone.equalsIgnoreCase("1")) {
                                Cursor phones = getContentResolver().query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                        null, null);
                                phones.moveToFirst();
                                cNumber = phones.getString(phones.getColumnIndex("data1"));
                                System.out.println("number is:" + cNumber);
                                txtphno.setText("Phone Number is: "+cNumber);
                            }
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            txtname.setText("Name is: "+name);

                            if (cNumber.compareTo("N/A") != 0) {
                                System.out.println(cNumber);
                                contacts_name.add(name);
                                contacts_number.add(cNumber);
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
}