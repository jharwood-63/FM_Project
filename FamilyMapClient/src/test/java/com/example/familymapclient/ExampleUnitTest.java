package com.example.familymapclient;

import org.junit.Test;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import data.DataCache;
import data.ServerProxy;
import model.Event;
import model.Person;
import model.User;
import requests.LoginRequest;
import requests.RegisterRequest;
import requests.UserRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.Result;
import viewmodels.SearchActivityHelper;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void dataCacheTest() throws IOException {
        DataCache dataCache = DataCache.getInstance();

        dataCache.setAuthToken("ced05a8a-cf08-409e-8cbd-d6b284d3d467");
        User testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila", "Parker", "f", "e6fa2c63-3736-49bb-b6a3-84b230c588cf");

        dataCache.fillDataCache();
        /*
        for (Map.Entry<String, Person> entry : dataCache.getPersonById().entrySet()) {
            assertEquals();
        }

         */
    }

    @Test
    public void loginTestPositive() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            URL loginUrl = new URL("http://localhost:7979/user/login");
            UserRequest loginRequest = new LoginRequest("sheila", "parker");

            Result result = serverProxy.doPost(loginUrl, loginRequest);

            assertTrue(result.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform post");
        }
    }

    @Test
    public void loginTestNegative() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            URL loginUrl = new URL("http://localhost:7979/user/login");
            UserRequest loginRequest = new LoginRequest("username", "password");

            Result result = serverProxy.doPost(loginUrl, loginRequest);

            assertFalse(result.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform post");
        }
    }

    @Test
    public void registerTestPositive() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            URL registerUrl = new URL("http://localhost:7979/user/register");
            RegisterRequest registerRequest = new RegisterRequest("username", "password", "yourmom@gmail.com", "jackson", "harwood", "m");

            Result result = serverProxy.doPost(registerUrl, registerRequest);

            assertTrue(result.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform post");
        }
    }

    @Test
    public void registerTestNegative() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            URL registerUrl = new URL("http://localhost:7979/user/register");
            RegisterRequest registerRequest = new RegisterRequest("sheila", "password", "yourmom@gmail.com", "jackson", "harwood", "m");

            Result result = serverProxy.doPost(registerUrl, registerRequest);

            assertFalse(result.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform post");
        }
    }

    @Test
    public void getPersonsTestPositive() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            String personUrl = "http://localhost:7979/person";
            String authToken = "40bfbfe4-61cd-4b6e-8a5c-c88ac2efd00f";

            PersonResult personResult = (PersonResult) serverProxy.doGet(personUrl, authToken);

            assertNotEquals(0, personResult.getData().length);
            assertTrue(personResult.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform get");
        }
    }

    @Test
    public void getPersonsTestNegative() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            String personUrl = "http://localhost:7979/person";
            String authToken = "40bfbfe4-61cd-4b6e-8a5c-c88ac2efd0";

            Result personResult = serverProxy.doGet(personUrl, authToken);

            assertFalse(personResult.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform get");
        }
    }

    @Test
    public void getEventsTestPositive() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            String eventUrl = "http://localhost:7979/event";
            String authToken = "40bfbfe4-61cd-4b6e-8a5c-c88ac2efd00f";

            EventResult eventResult = (EventResult) serverProxy.doGet(eventUrl, authToken);

            assertNotEquals(0, eventResult.getData().length);
            assertTrue(eventResult.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform get");
        }
    }

    @Test
    public void getEventsTestNegative() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            String eventUrl = "http://localhost:7979/event";
            String authToken = "40bfbfe4-61cd-4b6e-8a5c-c88ac2efd0";

            Result eventResult = serverProxy.doGet(eventUrl, authToken);

            assertFalse(eventResult.isSuccess());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform get");
        }
    }

    @Test
    public void sortEventsTest1() {
        DataCache dataCache = DataCache.getInstance();

        Event event1 = new Event("Sheila_Birth", "sheila", "Sheila_Parker",	(float) -36.1833000183105, (float) 144.966705322266, "Australia", "Melbourne", "birth", 1970);
        Event event2 = new Event("Sheila_Marriage", "sheila", "Sheila_Parker",	(float) 34.0499992370605, (float) -117.75, "United States", "Los Angeles", "marriage", 2012);
        Event event3 = new Event("Sheila_Asteroids", "sheila", "Sheila_Parker",	(float) 77.4666976928711, (float) -68.7667007446289, "Denmark", "Qaanaaq", "completed asteroids", 2014);
        Event event4 = new Event("Other_Asteroids", "sheila", "Sheila_Parker",	(float) 74.4666976928711, (float) -60.7667007446289, "Denmark", "Qaanaaq", "COMPLETED ASTEROIDS", 2014);
        Event event5 = new Event("Sheila_Death", "sheila", "Sheila_Parker",	(float) 40.2444000244141, (float) 111.660797119141, "China", "Hohhot", "death", 2015);

        List<Event> sortedEvents = new ArrayList<>();
        sortedEvents.add(event1);
        sortedEvents.add(event2);
        sortedEvents.add(event3);
        sortedEvents.add(event4);
        sortedEvents.add(event5);

        List<Event> unsortedEvents = new ArrayList<>();
        unsortedEvents.add(event3);
        unsortedEvents.add(event5);
        unsortedEvents.add(event1);
        unsortedEvents.add(event4);
        unsortedEvents.add(event2);

        List<Event> test = dataCache.sortEventsByYear(unsortedEvents);

        for (int i = 0; i < test.size(); i++) {
            assertEquals(sortedEvents.get(i), test.get(i));
        }
    }

    @Test
    public void sortEventsTest2() {
        DataCache dataCache = DataCache.getInstance();

        Event event2 = new Event("Sheila_Marriage", "sheila", "Sheila_Parker",	(float) 34.0499992370605, (float) -117.75, "United States", "Los Angeles", "marriage", 2012);
        Event event3 = new Event("Sheila_Asteroids", "sheila", "Sheila_Parker",	(float) 77.4666976928711, (float) -68.7667007446289, "Denmark", "Qaanaaq", "completed asteroids", 2014);
        Event event4 = new Event("Other_Asteroids", "sheila", "Sheila_Parker",	(float) 74.4666976928711, (float) -60.7667007446289, "Denmark", "Qaanaaq", "COMPLETED ASTEROIDS", 2014);

        List<Event> sortedEvents = new ArrayList<>();
        sortedEvents.add(event2);
        sortedEvents.add(event3);
        sortedEvents.add(event4);

        List<Event> unsortedEvents = new ArrayList<>();
        unsortedEvents.add(event3);
        unsortedEvents.add(event4);
        unsortedEvents.add(event2);

        List<Event> test = dataCache.sortEventsByYear(unsortedEvents);

        for (int i = 0; i < test.size(); i++) {
            assertEquals(sortedEvents.get(i), test.get(i));
        }
    }

    @Test
    public void searchTest1() {
        SearchActivityHelper helper = new SearchActivityHelper();

        Event event1 = new Event("Sheila_Birth", "sheila", "Sheila_Parker",	(float) -36.1833000183105, (float) 144.966705322266, "Australia", "Melbourne", "birth", 1970);
        Event event2 = new Event("Sheila_Marriage", "sheila", "Sheila_Parker",	(float) 34.0499992370605, (float) -117.75, "United States", "Los Angeles", "marriage", 2012);
        Event event3 = new Event("Sheila_Asteroids", "sheila", "Sheila_Parker",	(float) 77.4666976928711, (float) -68.7667007446289, "Denmark", "Qaanaaq", "completed asteroids", 2014);
        Event event4 = new Event("Other_Asteroids", "sheila", "Sheila_Parker",	(float) 74.4666976928711, (float) -60.7667007446289, "Denmark", "Qaanaaq", "COMPLETED ASTEROIDS", 2014);
        Event event5 = new Event("Sheila_Death", "sheila", "Sheila_Parker",	(float) 40.2444000244141, (float) 111.660797119141, "China", "Hohhot", "death", 2015);

        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);

        Person person1 = new Person("Sheila_Parker", "sheila", "Sheila", "Parker", "f", "Blaine_McGary", "Betty_White",	"Davis_Hyer");
        Person person2 = new Person("Davis_Hyer", "sheila", "Davis", "Hyer", "m", null, null,	"Sheila_Parker");
        Person person3 = new Person("Blaine_McGary", "sheila", "Blaine", "McGary", "m", "Ken_Rodham", "Mrs_Rodham",	"Betty_White");
        Person person4 = new Person("Betty_White", "sheila", "Betty", "White", "f", "Frank_Jones", "Mrs_Jones",	"Blaine_McGary");
        Person person5 = new Person("Ken_Rodham", "sheila", "Ken", "Rodham", "m", null, null,	"Mrs_Rodham");
        Person person6 = new Person("Mrs_Rodham", "sheila", "Mrs", "Rodham", "f", null, null,	"Ken_Rodham");
        Person person7 = new Person("Frank_Jones", "sheila", "Frank", "Jones", "m", null, null,	"Mrs_Jones");
        Person person8 = new Person("Mrs_Jones", "sheila", "Mrs", "Jones", "f", null, null,	"Frank_Jones");

        List<Person> persons = new ArrayList<>();
        persons.add(person1);
        persons.add(person2);
        persons.add(person3);
        persons.add(person4);
        persons.add(person5);
        persons.add(person6);
        persons.add(person7);
        persons.add(person8);

        String searchKey1 = "den";
        String searchKey2 = "Mrs";
        String searchKey3 = "yourmom";

        List<Event> searchedEvents = helper.searchEvents(events, searchKey1);
        List<Person> searchedPersons = helper.searchPersons(persons, searchKey2);

        List<Event> emptyEvents = helper.searchEvents(events, searchKey3);
        List<Person> emptyPersons = helper.searchPersons(persons, searchKey3);

        assertEquals(2, searchedEvents.size());
        assertEquals(2, searchedPersons.size());
        assertEquals(0, emptyEvents.size());
        assertEquals(0, emptyPersons.size());
    }
}