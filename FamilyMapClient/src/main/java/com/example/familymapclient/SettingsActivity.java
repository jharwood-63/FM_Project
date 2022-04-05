package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat lifeStory = (SwitchCompat) findViewById(R.id.lifeStorySwitch);
        SwitchCompat familyTree = (SwitchCompat) findViewById(R.id.familyTreeSwitch);
        SwitchCompat spouse = (SwitchCompat) findViewById(R.id.spouseSwitch);
        SwitchCompat fatherSide = (SwitchCompat) findViewById(R.id.fatherSideSwitch);
        SwitchCompat motherSide = (SwitchCompat) findViewById(R.id.motherSideSwitch);
        SwitchCompat maleEvents = (SwitchCompat) findViewById(R.id.maleEventsSwitch);
        SwitchCompat femaleEvents = (SwitchCompat) findViewById(R.id.femaleEventsSwitch);
        Button logoutButton = (Button) findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


    }

    private void logout() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.login_key), false);
        startActivity(intent);
    }
}