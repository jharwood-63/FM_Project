package com.example.familymapclient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Map;
import java.util.Set;

import data.DataCache;
import model.Event;
import model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private DataCache dataCache = DataCache.getInstance();

    /*Colors*/
    private static final float BIRTH_COLOR = BitmapDescriptorFactory.HUE_BLUE;
    private static final float MARRIAGE_COLOR = BitmapDescriptorFactory.HUE_YELLOW;
    private static final float DEATH_COLOR = BitmapDescriptorFactory.HUE_RED;
    //FIXME: need to add colors for the other events, this is just a default one that needs to be changed
    private static final float DEFAULT_COLOR = BitmapDescriptorFactory.HUE_GREEN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapLoaded() {/*probably won't need this*/}

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        placeMarkers();


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
        else {
            color = DEFAULT_COLOR;
        }

        return color;
    }

    private void createLines() {
        //get the selected event?

        // spouse -> selected event to spouse's birth

        //find the spouse birth

        //  get the personID for the selected event
        //  use personID to get the person
        //  get the spouseID of that person
        //  get the events associated with the spouseID
        //  get the birth event

        /* family tree
         * selected event to father's birth event, or earliest event
         * selected event to mother's birth event, or earliest event
         * from each birth event to parents birth event, or earliest event
         */
        // life story -> connect each event in life story
    }

    private Event getBirthEvent(String personID) {
        Person eventPerson = dataCache.getPerson(personID);
        String spouseID = eventPerson.getSpouseID();

        Map<String, Set<Event>> personEvents = dataCache.getPersonEvents();
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

    private void drawLine(Event startEvent, Event endEvent, float lineColor, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        int color = (int) lineColor;

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(color)
                .width(width);
        Polyline line = map.addPolyline(options);
    }
}