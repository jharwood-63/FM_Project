package com.example.familymapclient;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import data.DataCache;
import model.Event;
import model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    /*Colors*/
    //Markers
    private static final float BIRTH_COLOR = BitmapDescriptorFactory.HUE_BLUE;
    private static final float MARRIAGE_COLOR = BitmapDescriptorFactory.HUE_YELLOW;
    private static final float DEATH_COLOR = BitmapDescriptorFactory.HUE_RED;
    private static final float BAPTISM_COLOR = BitmapDescriptorFactory.HUE_CYAN;
    private static final float RETIREMENT_COLOR = BitmapDescriptorFactory.HUE_MAGENTA;
    private static final float FIRST_KISS_COLOR = BitmapDescriptorFactory.HUE_ROSE;
    private static final float DEFAULT_COLOR = BitmapDescriptorFactory.HUE_GREEN;

    private GoogleMap map;
    private View view;
    private Event selectedEvent = null;
    private DataCache dataCache = DataCache.getInstance();
    private Set<Polyline> lines = new HashSet<>();

    TextView personName;
    TextView location;
    ImageView genderImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        personName = (TextView) view.findViewById(R.id.personNameText);
        location = (TextView) view.findViewById(R.id.locationNameText);
        genderImageView = (ImageView) view.findViewById(R.id.genderIcon);

        if (selectedEvent == null) {
            personName.setText(getString(R.string.await_click));
            location.setText("");
        }

        return view;
    }

    @Override
    public void onMapLoaded() {/*probably won't need this*/}

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        placeMarkers();

        /*
         * Create an onClickListener
         * when the marker is clicked use get tag to get the event
         * draw lines
         */
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                selectedEvent = (Event) marker.getTag();
                Person eventPerson = dataCache.getPerson(selectedEvent.getPersonID());
                removeLines();
                createLines(selectedEvent);

                personName.setText(getString(R.string.person_name, eventPerson.getFirstName(), eventPerson.getLastName()));
                location.setText(getString(R.string.location_name, selectedEvent.getEventType().toUpperCase(), selectedEvent.getCity(), selectedEvent.getCountry()));

                String personGender = eventPerson.getGender();

                switch (personGender) {
                    case "m":
                        Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                                colorRes(R.color.male_icon).sizeDp(40);
                        genderImageView.setImageDrawable(maleIcon);
                        break;
                    case "f":
                        Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                                colorRes(R.color.female_icon).sizeDp(40);
                        genderImageView.setImageDrawable(femaleIcon);
                }

                return false;
            }
        });

    }

    private void placeMarkers() {
        DataCache dataCache = DataCache.getInstance();

        Map<String, Event> eventMap = dataCache.getEventById();

        for (Map.Entry<String, Event> eventEntry : eventMap.entrySet()) {
            Event event = eventEntry.getValue();

            float markerColor = decideColor(event.getEventType());

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

            marker.setTag(event);
        }
    }

    private float decideColor(String eventType) {
        float color;

        if (eventType.equalsIgnoreCase(getString(R.string.birth_event))) {
            color = BIRTH_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.marriage_event))) {
            color = MARRIAGE_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.death_event))) {
            color = DEATH_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.baptism_event))) {
            color = BAPTISM_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.retirement_event))) {
            color = RETIREMENT_COLOR;
        }
        else if (eventType.equalsIgnoreCase(getString(R.string.first_kiss_event))) {
            color = FIRST_KISS_COLOR;
        }
        else {
            color = DEFAULT_COLOR;
        }

        return color;
    }

    private void createLines(Event selectedEvent) {
        Map<String, Set<Event>> personEvents = dataCache.getPersonEvents();
        // spouse -> selected event to spouse's birth
        // find the spouse birth
        Event spouseBirthEvent = getSpouseBirthEvent(selectedEvent.getPersonID(), personEvents);
        drawLine(selectedEvent, spouseBirthEvent, getActivity().getResources().getColor(R.color.spouse_line), 10);

        /* family tree
         * selected event to father's birth event, or earliest event
         * selected event to mother's birth event, or earliest event
         * from each birth event to parents birth event, or earliest event
         */
        // life story -> connect each event in life story, chronologically
        /*
         * Birth events, if present, are always first
         * Events sorted primarily by year, and secondarily by event type
         * normalized to lower-case
         * Death events, if present, are always last
         */

        Set<Event> events = personEvents.get(selectedEvent.getPersonID());

        Event[] sortedEvents = sortEvents(events);

        float color = getActivity().getResources().getColor(R.color.life_line);
        drawLine(sortedEvents[0], sortedEvents[1], color, 10);
        for (int i = 1; i < sortedEvents.length; i++) {
            if (i != (sortedEvents.length - 1)) {
                drawLine(sortedEvents[1], sortedEvents[i + 1], color, 10);
            }
        }
    }

    private Event getSpouseBirthEvent(String personID, Map<String, Set<Event>> personEvents) {
        Person eventPerson = dataCache.getPerson(personID);
        String spouseID = eventPerson.getSpouseID();

        Set<Event> events = personEvents.get(spouseID);

        Event spouseBirth = null;
        for (Event event : events) {
            if (event.getEventType().equals(getString(R.string.birth_event))) {
                spouseBirth = event;
                break;
            }
        }

        return spouseBirth;
    }

    private Event[] sortEvents(Set<Event> events) {
        Event[] sortedEvents = new Event[events.size()];
        sortedEvents = events.toArray(sortedEvents);

        Event temp;
        for (int i = 0; i < sortedEvents.length; i++) {
            for (int j = 1; j < sortedEvents.length - i; j++) {
                if (sortedEvents[j-1].getYear() < sortedEvents[j].getYear()) {
                    temp = sortedEvents[j-1];
                    sortedEvents[j-1] = sortedEvents[j];
                    sortedEvents[j] = temp;
                }
            }
        }

        return sortedEvents;
    }

    private Event findEvent(Set<Event> events, String eventType) {
        for (Event event : events) {
            if (event.getEventType().equalsIgnoreCase(eventType)) {
                return event;
            }
        }

        return null;
    }

    private void drawLine(Event startEvent, Event endEvent, float lineColor, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        int color = (int) lineColor;

        Polyline line = map.addPolyline(new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(color)
                .width(width));

        lines.add(line);
    }

    private void removeLines() {
        for (Polyline line : lines) {
            line.remove();
        }

        lines.clear();
    }
}