package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.DataCache;
import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {
    private DataCache dataCache;
    Map<String, Person> personById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        String personID = intent.getStringExtra("personID");

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        dataCache = DataCache.getInstance();
        personById = dataCache.getPersonById();

        expandableListView.setAdapter(new ExpandableListAdapter(personID));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private Map<String, Person> immediateFamily;
        private Set<Event> usedEvents;

        ExpandableListAdapter(String personID) {
            Set<Event> filteredEvents = dataCache.getFilteredEvents();
            usedEvents = getUsedEvents(filteredEvents, personID);
            immediateFamily = getImmediateFamily(personID);
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return usedEvents.size();
                case FAMILY_GROUP_POSITION:
                    return immediateFamily.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.event_list_title);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.family_list_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.life_event_item, parent, false);
                    //FIXME: CALL AN INITIALIZE FUNCTION FOR EVENTS
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_item, parent, false);
                    //FIXME: CALL AN INITIALIZE FUNCTION FOR EVENTS
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View lifeEventItemView, final int childPosition) {

        }

        private void initializeFamilyView(View familyItemView, final int childPosition) {

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private Map<String, Person> getImmediateFamily(String personID) {
        Map<String, Person> immediateFamily = new HashMap<>();
        Person person = personById.get(personID);

        immediateFamily.put("Father", personById.get(person.getFatherID()));
        immediateFamily.put("Mother", personById.get(person.getMotherID()));
        immediateFamily.put("Spouse", personById.get(person.getSpouseID()));
        immediateFamily.put("Child", getChild(person));

        return immediateFamily;
    }

    private Person getChild(Person person) {
        for (Map.Entry<String, Person> personEntry : personById.entrySet()) {
            if (personEntry.getValue().getMotherID().equals(person.getPersonID()) || personEntry.getValue().getFatherID().equals(person.getPersonID())) {
                return personEntry.getValue();
            }
        }

        return null;
    }

    private Set<Event> getUsedEvents(Set<Event> filteredEvents, String personID) {
        Map<String, Event> eventById = dataCache.getEventById();
        Set<Event> usedEvents = new HashSet<>();

        for (Map.Entry<String, Event> eventEntry : eventById.entrySet()) {
            Event event = eventEntry.getValue();
            if (filteredEvents.contains(event) && event.getPersonID().equals(personID)) {
                usedEvents.add(event);
            }
        }

        return usedEvents;
    }
}