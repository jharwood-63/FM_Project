package com.example.familymapclient;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
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
import viewmodels.MapViewModel;
import viewmodels.SettingsActivityViewModel;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private final Set<Polyline> lines = new HashSet<>();
    private final DataCache dataCache = DataCache.getInstance();
    private Set<Event> filteredEvents = new HashSet<>();
    private final Map<String, Set<Event>> personEvents = dataCache.getPersonEvents();
    private final SettingsActivityViewModel settingsActivityViewModel = SettingsActivityViewModel.getInstance();
    private final MapViewModel mapViewModel = MapViewModel.getInstance();

    private TextView personName;
    private TextView location;
    private ImageView genderImageView;
    private LinearLayout mapTextLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        personName = (TextView) view.findViewById(R.id.personNameText);
        location = (TextView) view.findViewById(R.id.locationNameText);
        genderImageView = (ImageView) view.findViewById(R.id.genderIcon);
        mapTextLayout = (LinearLayout) view.findViewById(R.id.mapTextLayout);

        if (mapViewModel.getSelectedEvent() == null) {
            resetTextView();
        }

        mapTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapViewModel.getSelectedEvent() != null) {
                    //switch fragments, pass the data that it needs
                    //should not switch if no marker is selected
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapViewModel.getSelectedEvent() != null) {
            Person eventPerson = dataCache.getPerson(mapViewModel.getSelectedEvent().getPersonID());
            mapViewModel.getMap().clear();

            placeMarkers();

            setTextView(eventPerson);
            createLines(mapViewModel.getSelectedEvent());
        }
    }

    @Override
    public void onMapLoaded() {/*probably won't need this*/}

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapViewModel.setMap(googleMap);
        mapViewModel.getMap().setOnMapLoadedCallback(this);

        placeMarkers();

        mapViewModel.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                mapViewModel.setSelectedEvent((Event) marker.getTag());

                Person eventPerson = dataCache.getPerson(mapViewModel.getSelectedEvent().getPersonID());
                removeLines();
                createLines(mapViewModel.getSelectedEvent());

                setTextView(eventPerson);

                return false;
            }
        });
    }

    private void placeMarkers() {
        filteredEvents = getFilteredEvents();
        dataCache.setFilteredEvents(filteredEvents);

        for (Event event : filteredEvents) {
            float markerColor = decideColor(event.getEventType());

            Marker marker = mapViewModel.getMap().addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

            marker.setTag(event);
        }
    }

    private Set<Event> getFilteredEvents() {
        Set<Event> eventsFiltered;

        boolean fatherSide = settingsActivityViewModel.isFatherSideEnabled();
        boolean motherSide = settingsActivityViewModel.isMotherSideEnabled();
        boolean maleEvents = settingsActivityViewModel.isMaleEventsEnabled();
        boolean femaleEvents = settingsActivityViewModel.isFemaleEventsEnabled();

        if (fatherSide && !motherSide) {
            eventsFiltered = filterPaternal(maleEvents, femaleEvents);
        }
        else if (motherSide && !fatherSide) {
            eventsFiltered = filterMaternal(maleEvents, femaleEvents);
        }
        else if (fatherSide && motherSide) {
            eventsFiltered = filterPaternal(maleEvents, femaleEvents);
            eventsFiltered.addAll(filterMaternal(maleEvents, femaleEvents));
        }
        else {
            eventsFiltered = filterPaternal(maleEvents, femaleEvents);
            eventsFiltered.addAll(filterMaternal(maleEvents, femaleEvents));
        }

        return eventsFiltered;
    }

    private Set<Event> filterPaternal(boolean maleEvents, boolean femaleEvents) {
        Set<Event> eventsFiltered = new HashSet<>();

        Set<String> paternalMales = dataCache.getPaternalMales();
        Set<String> paternalFemales = dataCache.getPaternalFemales();

        if (maleEvents && !femaleEvents) {
            fillFilteredEvents(paternalMales, eventsFiltered);
        }
        else if (femaleEvents && !maleEvents) {
            fillFilteredEvents(paternalFemales, eventsFiltered);
        }
        else if (maleEvents && femaleEvents) {
            fillFilteredEvents(paternalMales, eventsFiltered);
            fillFilteredEvents(paternalFemales, eventsFiltered);
        }

        return eventsFiltered;
    }

    private Set<Event> filterMaternal(boolean maleEvents, boolean femaleEvents) {
        Set<Event> eventsFiltered = new HashSet<>();

        Set<String> maternalMales = dataCache.getMaternalMales();
        Set<String> maternalFemales = dataCache.getMaternalFemales();

        if (maleEvents && !femaleEvents) {
            fillFilteredEvents(maternalMales, eventsFiltered);
        }
        else if (femaleEvents && !maleEvents) {
            fillFilteredEvents(maternalFemales, eventsFiltered);
        }
        else if (maleEvents && femaleEvents) {
            fillFilteredEvents(maternalFemales, eventsFiltered);
            fillFilteredEvents(maternalMales, eventsFiltered);
        }

        return eventsFiltered;
    }

    private void fillFilteredEvents(Set<String> personIDs, Set<Event> eventsFiltered) {
        for (String personID : personIDs) {
            eventsFiltered.addAll(personEvents.get(personID));
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

    private void createLines(Event selectedEvent) {
        int color = getResources().getColor(R.color.spouse_line);

        if (filteredEvents.contains(selectedEvent)) {
            if (settingsActivityViewModel.isSpouseLinesEnabled()) {
                Event spouseBirthEvent = getSpouseBirthEvent(selectedEvent.getPersonID());
                if (filteredEvents.contains(spouseBirthEvent)) {
                    drawLine(selectedEvent, spouseBirthEvent, color, 10);
                }
            }

            if (settingsActivityViewModel.isFamilyTreeEnabled()) {
                Person parent = dataCache.getPerson(selectedEvent.getPersonID());
                drawFamilyLines(parent, selectedEvent, 15);
            }

            if (settingsActivityViewModel.isLifeLinesEnabled()) {
                Set<Event> events = personEvents.get(selectedEvent.getPersonID());
                Event[] sortedEvents = sortEvents(events);

                color = getResources().getColor(R.color.life_line);
                if (sortedEvents.length > 1) {
                    if (filteredEvents.contains(sortedEvents[0]) && filteredEvents.contains(sortedEvents[1])) {
                        drawLine(sortedEvents[0], sortedEvents[1], color, 10);
                    }
                }

                for (int i = 1; i < sortedEvents.length; i++) {
                    if (i != (sortedEvents.length - 1) && filteredEvents.contains(sortedEvents[i])) {
                        drawLine(sortedEvents[i], sortedEvents[i + 1], color, 10);
                    }
                }
            }
        }
    }

    private Event getSpouseBirthEvent(String personID) {
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

    private void drawFamilyLines(Person parent, Event startEvent, int lineWidth) {
        Map<String, Person> personById = dataCache.getPersonById();
        Person father = personById.get(parent.getFatherID());
        Person mother = personById.get(parent.getMotherID());

        if (father != null && mother != null) {
            Set<Event> fatherEvents = personEvents.get(father.getPersonID());
            Set<Event> motherEvents = personEvents.get(mother.getPersonID());

            Event fatherBirthEvent = findEvent(fatherEvents, getString(R.string.birth_event));
            Event motherBirthEvent = findEvent(motherEvents, getString(R.string.birth_event));

            if (fatherBirthEvent != null && motherBirthEvent != null) {
                if (filteredEvents.contains(startEvent)) {
                    int color = getResources().getColor(R.color.family_line);

                    if (filteredEvents.contains(fatherBirthEvent)) {
                        drawLine(startEvent, fatherBirthEvent, color, lineWidth);
                    }

                    if (filteredEvents.contains(motherBirthEvent)) {
                        drawLine(startEvent, motherBirthEvent, color, lineWidth);
                    }
                }

                drawFamilyLines(father, fatherBirthEvent, lineWidth - 3);
                drawFamilyLines(mother, motherBirthEvent, lineWidth - 3);
            }
        }
    }

    private Event findEvent(Set<Event> events, String eventType) {
        for (Event event : events) {
            if (event.getEventType().equalsIgnoreCase(eventType)) {
                return event;
            }
        }

        return null;
    }

    private Event[] sortEvents(Set<Event> events) {
        Event[] sortedEvents = new Event[events.size()];
        sortedEvents = events.toArray(sortedEvents);

        Event temp;
        for (int i = 0; i < sortedEvents.length; i++) {
            for (int j = 1; j < sortedEvents.length - i; j++) {
                if (sortedEvents[j-1].getYear() > sortedEvents[j].getYear()) {
                    temp = sortedEvents[j-1];
                    sortedEvents[j-1] = sortedEvents[j];
                    sortedEvents[j] = temp;
                }
            }
        }

        return sortedEvents;
    }

    private void drawLine(Event startEvent, Event endEvent, int lineColor, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        Polyline line = mapViewModel.getMap().addPolyline(new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(lineColor)
                .width(width));

        lines.add(line);
    }

    private void setTextView(Person eventPerson) {
        if (filteredEvents.contains(mapViewModel.getSelectedEvent())) {
            personName.setText(getString(R.string.person_name, eventPerson.getFirstName(), eventPerson.getLastName()));
            location.setText(getString(R.string.location_name, mapViewModel.getSelectedEvent().getEventType().toUpperCase(),
                    mapViewModel.getSelectedEvent().getCity(), mapViewModel.getSelectedEvent().getCountry()));

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
        }
        else {
            resetTextView();
        }
    }

    private void resetTextView() {
        Drawable androidIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                colorRes(R.color.android_icon).sizeDp(40);
        genderImageView.setImageDrawable(androidIcon);

        personName.setText(getString(R.string.await_click));
        location.setText("");
    }

    private void removeLines() {
        for (Polyline line : lines) {
            line.remove();
        }

        lines.clear();
    }
}