package data;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;
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

    private Map<String, Person> personById;
    private Map<String, Event> eventById;

    //Key => personID, Value => list of all events associated with that person ID
    private Map<String, List<Event>> personEvents;

    private Set<String> paternalMales;
    private Set<String> paternalFemales;
    private Set<String> maternalMales;
    private Set<String> maternalFemales;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void fillDataCache() throws IOException {
        /*
        * api calls:
        * all persons associated with user => /person
        * all events associated with the user => /event
         */

        fillIdMaps("localhost:7979/person");
        fillIdMaps("localhost:7979/event");
    }

    private void fillIdMaps(String urlString) throws IOException {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.addRequestProperty("Authorization", this.authToken);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                Reader respBody = new InputStreamReader(inputStream);
                if (urlString.contains("person")) {
                    fillPersonById(respBody);
                }
                else if (urlString.contains("event")) {
                    fillEventById(respBody);
                }
            }
        }
        catch (IOException e) {
            throw new IOException("Error: unable to fill id maps");
        }
    }

    private void fillPersonById(Reader respBody) {
        Gson gson = new Gson();
        PersonResult personResult = (PersonResult) gson.fromJson(respBody, PersonResult.class);

        Person[] persons = personResult.getData();
        for (int i = 0; i < persons.length; i++) {
            personById.put(persons[i].getPersonID(), persons[i]);
        }
    }

    private void fillEventById(Reader respBody) {
        Gson gson = new Gson();
        EventResult eventResult = (EventResult) gson.fromJson(respBody, EventResult.class);

        Event[] events = eventResult.getData();
        for (int i = 0; i < events.length; i++) {
            eventById.put(events[i].getEventID(), events[i]);
        }
    }

    public Map<String, Person> getPersonById() {
        return personById;
    }

    public Map<String, Event> getEventById() {
        return eventById;
    }

    public Map<String, List<Event>> getPersonEvents() {
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
