package com.example.familymapclient;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import data.DataCache;
import model.Event;
import model.Person;
import viewmodels.MapViewModel;
import viewmodels.PersonActivityViewModel;
import viewmodels.SettingsActivityViewModel;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private Event selectedEvent;
    private Event activitySelectedEvent;
    private GoogleMap map;
    private final Set<Polyline> lines = new HashSet<>();
    private final DataCache dataCache = DataCache.getInstance();
    private List<Event> filteredEvents = new ArrayList<>();
    private final Map<String, Set<Event>> personEvents = dataCache.getPersonEvents();
    private final SettingsActivityViewModel settingsActivityViewModel = SettingsActivityViewModel.getInstance();
    private final MapViewModel mapViewModel = MapViewModel.getInstance();

    private TextView personName;
    private TextView location;
    private ImageView genderImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        selectedEvent = mapViewModel.getSelectedEvent();

        if (getArguments() != null) {
            Map<String, Event> eventById = dataCache.getEventById();
            activitySelectedEvent = eventById.get(getArguments().getString(getString(R.string.event_id)));
        }
        else {
            activitySelectedEvent = null;
        }

        personName = (TextView) view.findViewById(R.id.personNameText);
        location = (TextView) view.findViewById(R.id.locationNameText);
        genderImageView = (ImageView) view.findViewById(R.id.genderIcon);
        LinearLayout mapTextLayout = (LinearLayout) view.findViewById(R.id.mapTextLayout);

        if (selectedEvent == null) {
            resetTextView();
        }

        mapTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedEvent != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra("personID", selectedEvent.getPersonID());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        selectedEvent = mapViewModel.getSelectedEvent();

        if (selectedEvent == null && activitySelectedEvent == null) {
            resetMap(null, null);
        }
        else if (activitySelectedEvent == null) {
            Person eventPerson = dataCache.getPerson(selectedEvent.getPersonID());
            resetMap(eventPerson, selectedEvent);
        }
    }

    @Override
    public void onMapLoaded() {/*probably won't need this*/}

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        placeMarkers();

        if (activitySelectedEvent != null) {
            Person eventPerson = dataCache.getPerson(activitySelectedEvent.getPersonID());
            createLines(activitySelectedEvent);
            setTextView(eventPerson, activitySelectedEvent);

            LatLng latLng = new LatLng(activitySelectedEvent.getLatitude(), activitySelectedEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                selectedEvent = (Event) marker.getTag();
                mapViewModel.setSelectedEvent(selectedEvent);

                Person eventPerson = dataCache.getPerson(selectedEvent.getPersonID());
                removeLines();
                createLines(selectedEvent);

                setTextView(eventPerson, selectedEvent);

                return false;
            }
        });
    }

    private void resetMap(Person eventPerson, Event selectedEvent) {
        if (map != null) {
            map.clear();
            placeMarkers();
        }

        if (eventPerson != null) {
            setTextView(eventPerson, selectedEvent);
            createLines(selectedEvent);
        }
        else {
            resetTextView();
        }
    }

    private void placeMarkers() {
        filteredEvents = getFilteredEvents();
        dataCache.setFilteredEvents(filteredEvents);

        for (Event event : filteredEvents) {
            float markerColor = decideColor(event.getEventType());

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

            marker.setTag(event);
        }
    }

    private List<Event> getFilteredEvents() {
        List<Event> eventsFiltered;

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

    private List<Event> filterPaternal(boolean maleEvents, boolean femaleEvents) {
        List<Event> eventsFiltered = new ArrayList<>();

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

    private List<Event> filterMaternal(boolean maleEvents, boolean femaleEvents) {
        List<Event> eventsFiltered = new ArrayList<>();

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

    private void fillFilteredEvents(Set<String> personIDs, List<Event> eventsFiltered) {
        for (String personID : personIDs) {
            eventsFiltered.addAll(personEvents.get(personID));
        }
    }

    private float decideColor(String eventType) {
        Map<String, Float> newColors = dataCache.getOtherColors();
        int colorIndex = dataCache.getColorIndex();

        if (dataCache.getOtherColors().containsKey(eventType.toLowerCase())) {
            return newColors.get(eventType.toLowerCase());
        }
        else {
            dataCache.addColorToMap(eventType.toLowerCase(), colorIndex);
            dataCache.incrementColorIndex();
            return DataCache.OTHER_COLORS[colorIndex];
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
                List<Event> sortedEvents = dataCache.sortEventsByYear(new ArrayList<>(events));

                color = getResources().getColor(R.color.life_line);
                if (sortedEvents.size() > 1) {
                    if (filteredEvents.contains(sortedEvents.get(0)) && filteredEvents.contains(sortedEvents.get(1))) {
                        drawLine(sortedEvents.get(0), sortedEvents.get(1), color, 10);
                    }
                }

                for (int i = 1; i < sortedEvents.size(); i++) {
                    if (i != (sortedEvents.size() - 1) && filteredEvents.contains(sortedEvents.get(i))) {
                        drawLine(sortedEvents.get(i), sortedEvents.get(i + 1), color, 10);
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
        int color = getResources().getColor(R.color.family_line);
        Person father = personById.get(parent.getFatherID());
        Person mother = personById.get(parent.getMotherID());

        Set<Event> fatherEvents;
        Set<Event> motherEvents;
        Event fatherEarliestEvent;
        Event motherEarliestEvent;

        if (lineWidth == 0) {
            lineWidth += 5;
        }
        else if ((lineWidth - 4) <= 0) {
            lineWidth = (lineWidth * 2) + 2;
        }

        if (father != null) {
            fatherEvents = personEvents.get(father.getPersonID());
            fatherEarliestEvent = findEarliestEvent(fatherEvents);

            if (fatherEarliestEvent != null && filteredEvents.contains(startEvent) && filteredEvents.contains(fatherEarliestEvent)) {
                drawLine(startEvent, fatherEarliestEvent, color, lineWidth);
            }

            drawFamilyLines(father, fatherEarliestEvent, lineWidth - 4);
        }

        if (mother != null) {
            motherEvents = personEvents.get(mother.getPersonID());
            motherEarliestEvent = findEarliestEvent(motherEvents);

            if (motherEarliestEvent != null && filteredEvents.contains(startEvent) && filteredEvents.contains(motherEarliestEvent)) {
                drawLine(startEvent, motherEarliestEvent, color, lineWidth);
            }

            drawFamilyLines(mother, motherEarliestEvent, lineWidth - 4);
        }
    }

    private Event findEarliestEvent(Set<Event> events) {
        Event earliestEvent = events.iterator().next();

        for (Event event : events) {
            if (event.getYear() < earliestEvent.getYear()) {
                earliestEvent = event;
            }
        }

        return earliestEvent;
    }

    private void drawLine(Event startEvent, Event endEvent, int lineColor, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        Polyline line = map.addPolyline(new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(lineColor)
                .width(width));

        lines.add(line);
    }

    private void setTextView(Person eventPerson, Event selectedEvent) {
        if (filteredEvents.contains(selectedEvent)) {
            personName.setText(getString(R.string.person_name, eventPerson.getFirstName(), eventPerson.getLastName()));
            location.setText(getString(R.string.location_name, selectedEvent.getEventType().toUpperCase(),
                    selectedEvent.getCity(), selectedEvent.getCountry(), String.valueOf(selectedEvent.getYear())));

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