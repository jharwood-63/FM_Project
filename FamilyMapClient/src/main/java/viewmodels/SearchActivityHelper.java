package viewmodels;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class SearchActivityHelper {
    public List<Event> searchEvents(List<Event> events, String searchKey) {
        List<Event> searchedEvents = new ArrayList<>();

        for (Event event : events) {
            if (checkEvent(event, searchKey)) {
                searchedEvents.add(event);
            }
        }

        return searchedEvents;
    }

    public List<Person> searchPersons(List<Person> persons, String searchKey) {
        List<Person> searchedPersons = new ArrayList<>();

        for (Person person : persons) {
            if (checkPerson(person, searchKey.toLowerCase())) {
                searchedPersons.add(person);
            }
        }

        return searchedPersons;
    }

    private boolean checkEvent(Event event, String searchKey) {
        String country = event.getCountry().toLowerCase();
        String city = event.getCity().toLowerCase();
        String eventType = event.getEventType().toLowerCase();
        String year = String.valueOf(event.getYear()).toLowerCase();

        return country.contains(searchKey) || city.contains(searchKey) || eventType.contains(searchKey) || year.contains(searchKey);
    }

    private boolean checkPerson(Person person, String searchKey) {
        String firstName = person.getFirstName().toLowerCase();
        String lastName = person.getLastName().toLowerCase();

        return firstName.contains(searchKey) || lastName.contains(searchKey);
    }
}
