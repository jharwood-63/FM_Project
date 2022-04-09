package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import data.DataCache;
import viewmodels.SettingsActivityViewModel;

public class SettingsActivity extends AppCompatActivity {
    private final SettingsActivityViewModel settingsActivityViewModel = SettingsActivityViewModel.getInstance();

    private SwitchCompat lifeStory;
    private SwitchCompat familyTree;
    private SwitchCompat spouseLines;
    private SwitchCompat fatherSide;
    private SwitchCompat motherSide;
    private SwitchCompat maleEvents;
    private SwitchCompat femaleEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        lifeStory = (SwitchCompat) findViewById(R.id.lifeStorySwitch);
        familyTree = (SwitchCompat) findViewById(R.id.familyTreeSwitch);
        spouseLines = (SwitchCompat) findViewById(R.id.spouseSwitch);
        fatherSide = (SwitchCompat) findViewById(R.id.fatherSideSwitch);
        motherSide = (SwitchCompat) findViewById(R.id.motherSideSwitch);
        maleEvents = (SwitchCompat) findViewById(R.id.maleEventsSwitch);
        femaleEvents = (SwitchCompat) findViewById(R.id.femaleEventsSwitch);
        Button logoutButton = (Button) findViewById(R.id.logoutButton);

        setSwitches();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        lifeStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setLifeLinesEnabled(!settingsActivityViewModel.isLifeLinesEnabled());
            }
        });

        familyTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setFamilyTreeEnabled(!settingsActivityViewModel.isFamilyTreeEnabled());
            }
        });

        spouseLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setSpouseLinesEnabled(!settingsActivityViewModel.isSpouseLinesEnabled());
            }
        });

        fatherSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setFatherSideEnabled(!settingsActivityViewModel.isFatherSideEnabled());
            }
        });

        motherSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setMotherSideEnabled(!settingsActivityViewModel.isMotherSideEnabled());
            }
        });

        maleEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setMaleEventsEnabled(!settingsActivityViewModel.isMaleEventsEnabled());
            }
        });

        femaleEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivityViewModel.setFemaleEventsEnabled(!settingsActivityViewModel.isFemaleEventsEnabled());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(getString(R.string.login_key), true);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.login_key), true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void logout() {
        DataCache.getInstance().clearData();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.login_key), false);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setSwitches() {
        lifeStory.setChecked(settingsActivityViewModel.isLifeLinesEnabled());
        familyTree.setChecked(settingsActivityViewModel.isFamilyTreeEnabled());
        spouseLines.setChecked(settingsActivityViewModel.isSpouseLinesEnabled());
        fatherSide.setChecked(settingsActivityViewModel.isFatherSideEnabled());
        motherSide.setChecked(settingsActivityViewModel.isMotherSideEnabled());
        maleEvents.setChecked(settingsActivityViewModel.isMaleEventsEnabled());
        femaleEvents.setChecked(settingsActivityViewModel.isFemaleEventsEnabled());
    }
}