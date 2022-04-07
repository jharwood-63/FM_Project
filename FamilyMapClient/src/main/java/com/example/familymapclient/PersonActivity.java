package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.Map;
import java.util.Set;

import data.DataCache;
import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        DataCache dataCache = DataCache.getInstance();
        Set<Event> filteredEvents = dataCache.getFilteredEvents();
        Map<String, Person> immediateFamily = dataCache.getImmediateFamily()
    }
}