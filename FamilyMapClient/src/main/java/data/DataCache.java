package data;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;

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

    String authToken;

    Map<String, Person> personById;
    Map<String, Event> eventById;

    //Key => personID, Value => list of all events associated with that person ID
    Map<String, List<Event>> personEvents;

    Set<String> paternalMales;
    Set<String> paternalFemales;
    Set<String> maternalMales;
    Set<String> maternalFemales;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void fillDataCache() {
        /*
        * api calls:
        * all persons associated with user => /person
        * all events associated with the user => /event
         */

    }

    private void fillIdMaps(String urlString) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.addRequestProperty("Authorization", this.authToken);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            }
        }
    }
}
