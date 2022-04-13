package com.example.familymapclient;

import org.junit.Test;


import java.io.IOException;
import java.net.URL;

import data.DataCache;
import data.ServerProxy;
import model.User;
import requests.LoginRequest;
import requests.RegisterRequest;
import requests.UserRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.Result;

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
}