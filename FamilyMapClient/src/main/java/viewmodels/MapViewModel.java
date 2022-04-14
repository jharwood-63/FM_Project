package viewmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.DataCache;
import model.Event;

public class MapViewModel {
    private static MapViewModel instance;
    private final SettingsActivityViewModel settingsActivityViewModel = SettingsActivityViewModel.getInstance();
    private final DataCache dataCache = DataCache.getInstance();

    private Event selectedEvent = null;

    public static MapViewModel getInstance() {
        if (instance == null) {
            instance = new MapViewModel();
        }

        return instance;
    }

    private MapViewModel() {}

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Event> getFilteredEvents() {
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
        Map<String, Set<Event>> personEvents = dataCache.getPersonEvents();
        for (String personID : personIDs) {
            eventsFiltered.addAll(personEvents.get(personID));
        }
    }
}
