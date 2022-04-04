package com.example.familymapclient;

import org.junit.Test;


import java.io.IOException;
import java.net.URL;

import data.DataCache;
import data.ServerProxy;
import model.User;
import requests.LoginRequest;
import requests.UserRequest;

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
    public void postTest() throws IOException {
        ServerProxy serverProxy = new ServerProxy();
        try {
            URL loginUrl = new URL("http://10.0.2.2:7979/user/login");
            UserRequest loginRequest = new LoginRequest("u", "p");

            serverProxy.doPost(loginUrl, loginRequest);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error: unable to perform post");
        }
    }
}