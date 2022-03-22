package data;

import android.util.Log;

import com.example.familymapclient.ServerProxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;
import model.User;
import result.EventResult;
import result.PersonResult;

public class DataCache {
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
    private Person userPerson;

    private final Map<String, Person> personById = new HashMap<>();
    private final Map<String, Event> eventById = new HashMap<>();

    //Key => personID, Value => set of all events associated with that person ID
    private final Map<String, Set<Event>> personEvents = new HashMap<>();

    private final Set<String> paternalMales = new HashSet<>();
    private final Set<String> paternalFemales = new HashSet<>();
    private final Set<String> maternalMales = new HashSet<>();
    private final Set<String> maternalFemales = new HashSet<>();

    public void fillDataCache() throws IOException {
        ServerProxy serverProxy = new ServerProxy();

        PersonResult personResult = (PersonResult) serverProxy.doGet("http://10.0.2.2:7979/person", this.authToken);
        EventResult eventResult = (EventResult) serverProxy.doGet("http://10.0.2.2:7979/event", this.authToken);

        if (personResult.isSuccess() && eventResult.isSuccess()) {
            fillPersonById(personResult);
            fillEventById(eventResult);
        }
        else {
            Log.e("Data Cache", "ERROR IN THE DATABASE");
        }
    }

    public void sort() {
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

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setPerson(String personID) {
        this.userPerson = findPerson(personID);
    }

    public Person getUserPerson() {
        return this.userPerson;
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
