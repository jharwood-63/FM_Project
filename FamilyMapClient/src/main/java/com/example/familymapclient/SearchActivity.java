package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.DataCache;
import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {
    private static final int EVENT_ITEM_VIEW_TYPE = 0;
    private static final int PERSON_ITEM_VIEW_TYPE = 1;

    private final DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        List<Event> filteredEvents = dataCache.getFilteredEvents();
        List<Person> personList = dataCache.getPersonList();

        //List<Event> searchedEvents = searchEvents(filteredEvents);
        //List<Person> searchedPersons = searchPersons(personList);
        //EditText searchField = (EditText) findViewById(R.id.searchField);
        //convert the searchKey to lower case

        /* Search Algorith:
         * recycler view is empty before a search is executed
         * people come before events
         * Make a copy of both lists
         * make the lists smaller based on what is searched
         * send those lists to the recycler view
         */
        EditText searchField = (EditText) findViewById(R.id.searchField);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchKey = searchField.getText().toString().toLowerCase();

                List<Event> searchedEvents = searchEvents(filteredEvents, searchKey);
                List<Person> searchedPersons = searchPersons(personList, searchKey);

                EventPersonAdapter adapter = new EventPersonAdapter(searchedEvents, searchedPersons);
                recyclerView.setAdapter(adapter);
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

    private List<Event> searchEvents(List<Event> events, String searchKey) {
        List<Event> searchedEvent = new ArrayList<>();

        for (Event event : events) {
            if (checkEvent(event, searchKey)) {
                searchedEvent.add(event);
            }
        }

        return searchedEvent;
    }

    private List<Person> searchPersons(List<Person> persons, String searchKey) {
        List<Person> searchedPersons = new ArrayList<>();

        for (Person person : persons) {
            if (checkPerson(person, searchKey)) {
                searchedPersons.add(person);
            }
        }

        return searchedPersons;
    }

    private boolean checkEvent(Event event, String searchKey) {
        String country = event.getCountry().toLowerCase();
        String city = event.getCity().toLowerCase();
        String eventType = event.getEventType().toLowerCase();
        String year = String.valueOf(event.getYear()).toLowerCase();

        return country.contains(searchKey) || city.contains(searchKey) || eventType.contains(searchKey) || year.contains(searchKey);
    }

    private boolean checkPerson(Person person, String searchKey) {
        String firstName = person.getFirstName().toLowerCase();
        String lastName = person.getLastName().toLowerCase();

        return firstName.contains(searchKey) || lastName.contains(searchKey);
    }

    private class EventPersonAdapter extends RecyclerView.Adapter<EventPersonViewHolder> {
        private final List<Event> searchedEvents;
        private final List<Person> searchedPersons;

        EventPersonAdapter(List<Event> searchedEvents, List<Person> searchedPersons) {
            this.searchedEvents = searchedEvents;
            this.searchedPersons = searchedPersons;
        }

        @Override
        public int getItemViewType(int position) {
            return position < searchedEvents.size() ? EVENT_ITEM_VIEW_TYPE : PERSON_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public EventPersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == EVENT_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.life_event_item, parent, false);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.family_item, parent, false);
            }

            return new EventPersonViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull EventPersonViewHolder holder, int position) {
            if (position < searchedEvents.size()) {
                holder.bind(searchedEvents.get(position));
            }
            else {
                holder.bind(searchedPersons.get(position - searchedEvents.size()));
            }
        }

        @Override
        public int getItemCount() {
            return searchedEvents.size() + searchedPersons.size();
        }
    }

    private class EventPersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final int viewType;
        private Event event;
        private Person person;

        private final ImageView marker;
        private final TextView descriptionOne;
        private final TextView descriptionTwo;

        EventPersonViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == EVENT_ITEM_VIEW_TYPE) {
                marker = itemView.findViewById(R.id.eventMarker);
                descriptionOne = itemView.findViewById(R.id.eventDescription);
                descriptionTwo = itemView.findViewById(R.id.eventPerson);
            }
            else {
                marker = itemView.findViewById(R.id.genderMarker);
                descriptionOne = itemView.findViewById(R.id.ancestorName);
                descriptionTwo = null;
            }
        }

        private void bind(Event event) {
            this.event = event;
            int color = (int) decideColor(event.getEventType());
            marker.setColorFilter(color);
            descriptionOne.setText(getString(R.string.event_description, event.getEventType().toUpperCase(), event.getCity(),
                    event.getCountry(), String.valueOf(event.getYear())));
            descriptionTwo.setText(getString(R.string.family_person, person.getFirstName(), person.getLastName()));
        }

        private void bind(Person person) {
            this.person = person;
            marker.setImageDrawable(getIcon(person.getGender()));
            descriptionOne.setText(getString(R.string.family_person, person.getFirstName(), person.getLastName()));
        }

        @Override
        public void onClick(View view) {
            Intent intent;

            if (viewType == EVENT_ITEM_VIEW_TYPE) {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(getString(R.string.event_id), event.getEventID());
            }
            else {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("personID", person.getPersonID());
            }

            startActivity(intent);
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

        private Drawable getIcon(String gender) {
            Drawable icon;

            switch (gender) {
                case "m":
                    icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(40);
                    break;
                case "f":
                    icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(40);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid gender: " + gender);
            }

            return icon;
        }
    }
}