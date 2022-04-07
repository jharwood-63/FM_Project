package data;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;
import result.EventResult;
import result.PersonResult;

public class DataCache {
    public static final float BIRTH_COLOR = BitmapDescriptorFactory.HUE_BLUE;
    public static final float MARRIAGE_COLOR = BitmapDescriptorFactory.HUE_YELLOW;
    public static final float DEATH_COLOR = BitmapDescriptorFactory.HUE_RED;
    public static final float BAPTISM_COLOR = BitmapDescriptorFactory.HUE_CYAN;
    public static final float RETIREMENT_COLOR = BitmapDescriptorFactory.HUE_MAGENTA;
    public static final float FIRST_KISS_COLOR = BitmapDescriptorFactory.HUE_ROSE;
    public static final float[] OTHER_COLORS = new float[]{BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_VIOLET};

    private static DataCache instance;

    //synchronized key word makes this thread safe
    public synchronized static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache(){}

    private String authToken;
    private String userPersonID;

    private String personUrl;
    private String eventUrl;

    private Map<String, Float> otherColors = new HashMap<>();
    private int colorIndex;

    private final Map<String, Person> personById = new HashMap<>();
    private final Map<String, Event> eventById = new HashMap<>();

    //Key => personID, Value => set of all events associated with that person ID
    private final Map<String, Set<Event>> personEvents = new HashMap<>();

    private Set<Event> filteredEvents = new HashSet<>();

    private final Set<String> paternalMales = new HashSet<>();
    private final Set<String> paternalFemales = new HashSet<>();
    private final Set<String> maternalMales = new HashSet<>();
    private final Set<String> maternalFemales = new HashSet<>();

    public void fillDataCache() throws IOException {
        ServerProxy serverProxy = new ServerProxy();

        PersonResult personResult = (PersonResult) serverProxy.doGet(personUrl, this.authToken);
        EventResult eventResult = (EventResult) serverProxy.doGet(eventUrl, this.authToken);

        if (personResult.isSuccess() && eventResult.isSuccess()) {
            fillPersonById(personResult);
            fillEventById(eventResult);

            Person userPerson = findPerson(userPersonID);

            if (userPerson != null) {
                Person father = findPerson(userPerson.getFatherID());
                Person mother = findPerson(userPerson.getMotherID());

                paternalMales.add(father.getPersonID());
                maternalFemales.add(mother.getPersonID());

                sortPersons(father, "paternal");
                sortPersons(mother, "maternal");
            }

            sortEvents();
        }
        else {
            Log.e("Data Cache", "ERROR IN THE DATABASE");
        }
    }

    private void fillPersonById(PersonResult personResult) {
        Person[] persons = personResult.getData();

        for (Person person : persons) {
            personById.put(person.getPersonID(), person);
        }
    }

    private void fillEventById(EventResult eventResult) {
        Event[] events = eventResult.getData();

        for (Event event : events) {
            eventById.put(event.getEventID(), event);
        }
    }

    private void sortPersons(Person parent, String parentType) {
        Person father = findPerson(parent.getFatherID());
        Person mother = findPerson(parent.getMotherID());

        if (father != null) {
            sortPersons(father, parentType);
        }

        if (mother != null) {
            sortPersons(mother, parentType);
        }

        addToSet(parent, parentType);
    }

    private void addToSet(Person parent, String parentType) {
        if (parentType.equals("paternal")) {
            if (parent.getGender().equals("m")) {
                paternalMales.add(parent.getPersonID());
            }
            else if (parent.getGender().equals("f")) {
                paternalFemales.add(parent.getPersonID());
            }
        }
        else if (parentType.equals("maternal")) {
            if (parent.getGender().equals("m")) {
                maternalMales.add(parent.getPersonID());
            }
            else if (parent.getGender().equals("f")) {
                maternalFemales.add(parent.getPersonID());
            }
        }
    }

    private Person findPerson(String personID) {
        if (personID != null || !personID.equals("")) {
            for (Map.Entry<String, Person> pair : personById.entrySet()) {
                if (pair.getKey().equals(personID)) {
                    return pair.getValue();
                }
            }
        }

        return null;
    }

    private void sortEvents() {
        for (Map.Entry<String, Event> eventEntry : eventById.entrySet()) {
            String personID = eventEntry.getValue().getPersonID();
            if (personEvents.containsKey(personID)) {
                addToMap(personID, eventEntry.getValue());
            }
            else {
                Set<Event> events = new HashSet<>();
                events.add(eventEntry.getValue());
                personEvents.put(personID, events);
            }
        }
    }

    private void addToMap(String personID, Event event) {
        for (Map.Entry<String, Set<Event>> entry : personEvents.entrySet()) {
            if (entry.getKey().equals(personID)) {
                entry.getValue().add(event);
                break;
            }
        }
    }

    public void setUrls(String serverHost, String serverPort) {
        personUrl = "http://" + serverHost + ":" + serverPort + "/person";
        eventUrl = "http://" + serverHost + ":" + serverPort + "/event";
    }

    public Map<String, Float> getOtherColors() {
        return otherColors;
    }

    public void addToMap(String eventType, float color) {
        otherColors.put(eventType, color);
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void incrementColorIndex() {
        if (colorIndex < 3) {
            colorIndex++;
        }
        else {
            colorIndex = 0;
        }
    }

    public Set<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(Set<Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setPersonID(String personID) {
        this.userPersonID = personID;
    }

    public String getPersonID() {
        return this.userPersonID;
    }

    public Person getPerson(String personID) {
        return findPerson(personID);
    }

    public Map<String, Person> getPersonById() {
        return personById;
    }

    public Map<String, Event> getEventById() {
        return eventById;
    }

    public Map<String, Set<Event>> getPersonEvents() {
        return personEvents;
    }

    public Set<String> getPaternalMales() {
        return paternalMales;
    }

    public Set<String> getPaternalFemales() {
        return paternalFemales;
    }

    public Set<String> getMaternalMales() {
        return maternalMales;
    }

    public Set<String> getMaternalFemales() {
        return maternalFemales;
    }
}
