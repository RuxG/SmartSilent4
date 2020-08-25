package com.example.smartsilent.Contacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartsilent.Profile;
import com.example.smartsilent.R;

import java.util.ArrayList;
import java.util.List;


public class MakeContacts extends AppCompatActivity {
    Button btnview, listview, backButton;
    TextView txtname,txtphno;
    private Context context;
    private Profile mProfile;

    final private int PICK_CONTACT = 1;
    final private int REQUEST_MULTIPLE_PERMISSIONS = 124;

    ArrayList<String> contacts_name = new ArrayList<>();
    ArrayList<String> contacts_number = new ArrayList<>();

@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.make_contacts);

    // get profile name
    Bundle b = this.getIntent().getExtras();
    String profile_name = b.getString("profile_name");
    mProfile = b.getParcelable("profile");

    AccessContact();
    btnview = (Button) findViewById(R.id.btnload);
    listview = (Button) findViewById(R.id.lstload);
    txtname = (TextView) findViewById(R.id.txtname);
    txtphno = (TextView) findViewById(R.id.txtphno);

    // now we should find out how to create a database at a specific location
    // ex: profiles/profile_name/contacts.db

    context = getApplicationContext();


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
            b.putStringArrayList("contacts_name", (ArrayList<String>) mProfile.getContactsNames());
            b.putStringArrayList("contacts_number", (ArrayList<String>) mProfile.getContactsNumbers());

           // Pair<ArrayList<String>, ArrayList<String>> contacts = getContacts();
           // b.putStringArrayList("contacts_name", contacts.first);
           // b.putStringArrayList("contacts_number", contacts.second);
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


                               // System.out.println("number is:" + cNumber);
                               // txtphno.setText("Phone Number is: "+cNumber);
                            }
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            if(!mProfile.getContactsNames().contains(name) && cNumber.compareTo("N/A") != 0) {
                                mProfile.addContactName(name);
                                mProfile.addContactNumber(cNumber);
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("activity","contacts");
        intent.putExtra("profile", mProfile);
        setResult(Activity.RESULT_OK,intent);
        finish();//finishing activity
    }
}