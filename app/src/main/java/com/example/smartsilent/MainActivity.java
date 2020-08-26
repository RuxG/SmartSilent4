package com.example.smartsilent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.ADJUST_RAISE;

public class MainActivity extends AppCompatActivity{

    private ImageButton addProfile;
    private AudioManager audioManager;
    private Button mProfilesButton;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addProfile = findViewById(R.id.add_new_profile_button);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mProfilesButton = findViewById(R.id.my_profiles_button);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* check if profiles directory exists <=> if the user created any profile before
        if he didn't, create a profiles directory */
        Path path = FileSystems.getDefault().getPath(getApplicationContext().getFilesDir()+ "/profiles");
        File mydir;
        if (!Files.exists(path)) {
            mydir = new File(getApplicationContext().getFilesDir(), "profiles");
            mydir.mkdir();

            //File active_profiles = new File(getApplicationContext().getFilesDir() + "/profiles", "active_profiles");
           // active_profiles.mkdir();
        }

        if (!notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                //audioManager.adjustVolume(AudioManager.ADJUST_MUTE, 0);
                //audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, 0);
                Intent intent = new Intent(MainActivity.this, ProfileName.class);
                startActivity(intent);
            }
        });

        mProfilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyProfiles.class);
                startActivity(intent);
            }
        });
        checkAndRequestPermissions();

    }

    private  boolean checkAndRequestPermissions() {

        int readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int read_call_log = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        List listPermissionsNeeded = new ArrayList<>();
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (read_call_log != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}

/*
        if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        if (getApplicationContext().checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

 */