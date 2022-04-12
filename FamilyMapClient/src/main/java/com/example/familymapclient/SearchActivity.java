package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.w3c.dom.Text;

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


    }

    //EventPersonAdapter goes here

    private class EventPersonViewHandler extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final int viewType;
        private Event event;
        private Person person;

        private ImageView marker;
        private TextView descriptionOne;
        private TextView descriptionTwo;

        EventPersonViewHandler(View view, int viewType) {
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
            if (viewType == EVENT_ITEM_VIEW_TYPE) {
                //go to eventActivity
            }
            else {
                //go to personActivity
            }
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