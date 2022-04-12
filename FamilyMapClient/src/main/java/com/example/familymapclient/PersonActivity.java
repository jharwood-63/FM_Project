package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.DataCache;
import model.Event;
import model.Person;
import viewmodels.PersonActivityViewModel;

public class PersonActivity extends AppCompatActivity {
    private DataCache dataCache;
    private Map<String, Person> personById;

    private PersonActivityViewModel getViewModel() {
        return new ViewModelProvider(this).get(PersonActivityViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        dataCache = DataCache.getInstance();
        personById = dataCache.getPersonById();

        Intent intent = getIntent();
        String personID = intent.getStringExtra("personID");
        getViewModel().setPersonID(personID);

        initializeExpandableListAdapter(personID);
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

    private void initializeExpandableListAdapter(String personID) {
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

        private final List<Person> immediateFamily;
        private final List<Event> usedEvents;
        private final Map<String, String> immediateFamilyMap = new HashMap<>();
        private final String personID;

        ExpandableListAdapter(String personID) {
            List<Event> filteredEvents = dataCache.getFilteredEvents();
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
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View lifeEventItemView, final int childPosition) {
            Person person = personById.get(personID);
            Event event = usedEvents.get(childPosition);

            ImageView marker = lifeEventItemView.findViewById(R.id.eventMarker);
            int color = (int) decideColor(event.getEventType());
            marker.setColorFilter(color);

            TextView eventDescription = lifeEventItemView.findViewById(R.id.eventDescription);
            eventDescription.setText(getString(R.string.event_description, event.getEventType().toUpperCase(), event.getCity(),
                    event.getCountry(), String.valueOf(event.getYear())));

            TextView eventPerson = lifeEventItemView.findViewById(R.id.eventPerson);
            eventPerson.setText(getString(R.string.family_person, person.getFirstName(), person.getLastName()));

            lifeEventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra(getString(R.string.event_id), event.getEventID());
                    startActivity(intent);
                }
            });
        }

        private void initializeFamilyView(View familyItemView, final int childPosition) {
            Person familyMember = immediateFamily.get(childPosition);
            String relationship = immediateFamilyMap.get(familyMember.getPersonID());

            ImageView genderMarker = (ImageView) familyItemView.findViewById(R.id.genderMarker);
            genderMarker.setImageDrawable(getIcon(familyMember.getGender()));

            TextView ancestorName = (TextView) familyItemView.findViewById(R.id.ancestorName);
            ancestorName.setText(getString(R.string.family_person, familyMember.getFirstName(), familyMember.getLastName()));

            TextView relationshipName = (TextView) familyItemView.findViewById(R.id.relationshipName);
            relationshipName.setText(getString(R.string.relationship_name, relationship));

            familyItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra("personID", familyMember.getPersonID());
                    startActivity(intent);
                }
            });
        }

        private Drawable getIcon(String gender) {
            Drawable icon;

            switch (gender) {
                case "m":
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(40);
                    break;
                case "f":
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(40);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid gender: " + gender);
            }

            return icon;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private float decideColor(String eventType) {
            float[] resourceColors = new float[]{getResources().getColor(R.color.blue_color), getResources().getColor(R.color.yellow_color),
                    getResources().getColor(R.color.red_color), getResources().getColor(R.color.cyan_color), getResources().getColor(R.color.magenta_color),
                    getResources().getColor(R.color.rose_color), getResources().getColor(R.color.green_color), getResources().getColor(R.color.azure_color),
                    getResources().getColor(R.color.orange_color), getResources().getColor(R.color.violet_color)};

            Map<String, Integer> colors = dataCache.getResourceColors();
            int index = colors.get(eventType);

            return resourceColors[index];
        }

        private List<Person> getImmediateFamily(String personID) {
            List<Person> immediateFamily = new ArrayList<>();
            Person person = personById.get(personID);

            String fatherID = person.getFatherID();
            String motherID = person.getMotherID();
            String spouseID = person.getSpouseID();
            Person child = getChild(person);

            String fatherRelation = getString(R.string.father_relationship);
            String motherRelation = getString(R.string.mother_relationship);
            String spouseRelation = getString(R.string.spouse_relationship);
            String childRelation = getString(R.string.child_relationship);

            boolean isFatherMotherNull = isFatherMotherNull(fatherID, motherID);
            boolean isSpouseNull = isSpouseNull(spouseID);

            if (!isFatherMotherNull && child != null && !isSpouseNull) {
                immediateFamily.add(personById.get(fatherID));
                addToImmediateFamilyMap(fatherID, fatherRelation);

                immediateFamily.add(personById.get(motherID));
                addToImmediateFamilyMap(motherID, motherRelation);

                immediateFamily.add(personById.get(spouseID));
                addToImmediateFamilyMap(spouseID, spouseRelation);

                immediateFamily.add(child);
                addToImmediateFamilyMap(child.getPersonID(), childRelation);
            }
            else if (isFatherMotherNull && child != null && !isSpouseNull) {
                immediateFamily.add(personById.get(spouseID));
                addToImmediateFamilyMap(spouseID, spouseRelation);

                immediateFamily.add(child);
                addToImmediateFamilyMap(child.getPersonID(), childRelation);
            }
            else if (isFatherMotherNull && child == null && !isSpouseNull) {
                immediateFamily.add(personById.get(spouseID));
                addToImmediateFamilyMap(spouseID, spouseRelation);
            }
            else if (child == null && !isFatherMotherNull && !isSpouseNull) {
                immediateFamily.add(personById.get(fatherID));
                addToImmediateFamilyMap(fatherID, fatherRelation);

                immediateFamily.add(personById.get(motherID));
                addToImmediateFamilyMap(motherID, motherRelation);

                immediateFamily.add(personById.get(spouseID));
                addToImmediateFamilyMap(spouseID, spouseRelation);
            }
            else if (isFatherMotherNull && isSpouseNull && child != null) {
                immediateFamily.add(getChild(person));
                addToImmediateFamilyMap(child.getPersonID(), childRelation);
            }
            else if (child == null && isSpouseNull && !isFatherMotherNull) {
                immediateFamily.add(personById.get(fatherID));
                addToImmediateFamilyMap(fatherID, fatherRelation);

                immediateFamily.add(personById.get(motherID));
                addToImmediateFamilyMap(motherID, motherRelation);
            }
            else if (isSpouseNull && child != null && !isFatherMotherNull) {
                immediateFamily.add(personById.get(fatherID));
                addToImmediateFamilyMap(fatherID, fatherRelation);

                immediateFamily.add(personById.get(motherID));
                addToImmediateFamilyMap(motherID, motherRelation);

                immediateFamily.add(child);
                addToImmediateFamilyMap(child.getPersonID(), childRelation);
            }

            return immediateFamily;
        }

        private boolean isFatherMotherNull(String fatherID, String motherID) {
            return (fatherID == null || fatherID.equals("")) && (motherID == null || motherID.equals(""));
        }

        private boolean isSpouseNull(String spouseID) {
            return spouseID == null || spouseID.equals("");
        }

        private Person getChild(Person person) {
            for (Map.Entry<String, Person> personEntry : personById.entrySet()) {
                if (personEntry.getValue().getMotherID().equals(person.getPersonID()) || personEntry.getValue().getFatherID().equals(person.getPersonID())) {
                    return personEntry.getValue();
                }
            }

            return null;
        }

        private List<Event> getUsedEvents(List<Event> filteredEvents, String personID) {
            Map<String, Event> eventById = dataCache.getEventById();
            List<Event> usedEvents = new ArrayList<>();

            for (Map.Entry<String, Event> eventEntry : eventById.entrySet()) {
                Event event = eventEntry.getValue();
                if (filteredEvents.contains(event) && event.getPersonID().equals(personID)) {
                    usedEvents.add(event);
                }
            }

            sortEvents(usedEvents);

            return usedEvents;
        }

        private void sortEvents(List<Event> usedEvents) {
            Event temp;
            for (int i = 0; i < usedEvents.size(); i++) {
                for (int j = 1; j < usedEvents.size() - i; j++) {
                    if (usedEvents.get(j-1).getYear() > usedEvents.get(j).getYear()) {
                        temp = usedEvents.get(j-1);
                        usedEvents.set(j-1, usedEvents.get(j));
                        usedEvents.set(j, temp);
                    }
                }
            }
        }

        private void addToImmediateFamilyMap(String personID, String relationship) {
            immediateFamilyMap.put(personID, relationship);
        }
    }
}