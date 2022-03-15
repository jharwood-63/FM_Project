package com.example.familymapclient;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import data.DataCache;
import model.Person;
import model.User;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dataCacheTest() throws IOException {
        DataCache dataCache = DataCache.getInstance();

        dataCache.setAuthToken("ced05a8a-cf08-409e-8cbd-d6b284d3d467");
        User testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila", "Parker", "f", "e6fa2c63-3736-49bb-b6a3-84b230c588cf");
        dataCache.setUser(testUser);

        dataCache.fillDataCache();
        /*
        for (Map.Entry<String, Person> entry : dataCache.getPersonById().entrySet()) {
            assertEquals();
        }

         */
    }
}