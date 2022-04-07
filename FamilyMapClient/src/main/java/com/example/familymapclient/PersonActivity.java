package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

        dataCache = DataCache.getInstance();
        personById = dataCache.getPersonById();

        Intent intent = getIntent();
        String personID = intent.getStringExtra("personID");
        Person person = personById.get(personID);

        TextView firstNameView = (TextView) findViewById(R.id.firstName);
        TextView lastNameView = (TextView) findViewById(R.id.lastName);
        TextView genderView = (TextView) findViewById(R.id.personGender);

        firstNameView.setText(getString(R.string.personFirst, person.getFirstName()));
        lastNameView.setText(getString(R.string.personLast, person.getLastName()));
        genderView.setText(getString(R.string.personGender, getGenderString(person.getGender())));

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        expandableListView.setAdapter(new ExpandableListAdapter(personID));
    }

    private String getGenderString(String gender) {
        switch (gender) {
            case "m":
                return "Male";
            case "f":
                return "Female";
            default:
                throw new IllegalArgumentException("Invalid gender: " + gender);
        }
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final Map<String, Person> immediateFamily;
        private final List<Event> usedEvents;
        private String personID;

        ExpandableListAdapter(String personID) {
            Set<Event> filteredEvents = dataCache.getFilteredEvents();
            usedEvents = getUsedEvents(filteredEvents, personID);
            immediateFamily = getImmediateFamily(personID);
            this.personID = personID;
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
            Person person = personById.get(personID);
            Event event = usedEvents.get(childPosition);
            //set the marker color
            ImageView marker = lifeEventItemView.findViewById(R.id.eventMarker);
            int color = (int) decideColor(event.getEventType());
            marker.setColorFilter(color);
            //set the event description
            TextView eventDescription = lifeEventItemView.findViewById(R.id.eventDescription);
            eventDescription.setText(getString(R.string.event_description, event.getEventType().toUpperCase(), event.getCity(),
                    event.getCountry(), String.valueOf(event.getYear())));
            //set the event person name
            //set an onClickListener
        }

        private void initializeFamilyView(View familyItemView, final int childPosition) {

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private float decideColor(String eventType) {
        float color;

        if (eventType.equalsIgnoreCase(getString(R.string.birth_event))) {
            color = DataCache.BIRTH_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.marriage_event))) {
            color = DataCache.MARRIAGE_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.death_event))) {
            color = DataCache.DEATH_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.baptism_event))) {
            color = DataCache.BAPTISM_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.retirement_event))) {
            color = DataCache.RETIREMENT_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.first_kiss_event))) {
            color = DataCache.FIRST_KISS_COLOR;
        }
        else {
            color = decideOtherColor(eventType);
        }

        return color;
    }

    private float decideOtherColor(String eventType) {
        Map<String, Float> newColors = dataCache.getOtherColors();
        dataCache.incrementColorIndex();

        if (dataCache.getOtherColors().containsKey(eventType)) {
            return newColors.get(eventType);
        }
        else {
            dataCache.addToMap(eventType, DataCache.OTHER_COLORS[dataCache.getColorIndex()]);
            return DataCache.OTHER_COLORS[dataCache.getColorIndex()];
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

    private List<Event> getUsedEvents(Set<Event> filteredEvents, String personID) {
        Map<String, Event> eventById = dataCache.getEventById();
        List<Event> usedEvents = new ArrayList<>();

        for (Map.Entry<String, Event> eventEntry : eventById.entrySet()) {
            Event event = eventEntry.getValue();
            if (filteredEvents.contains(event) && event.getPersonID().equals(personID)) {
                usedEvents.add(event);
            }
        }

        return usedEvents;
    }
}