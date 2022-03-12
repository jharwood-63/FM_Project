package com.example.familymapclient;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import data.DataCache;
import model.Person;

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

        dataCache.fillDataCache();
        /*
        for (Map.Entry<String, Person> entry : dataCache.getPersonById().entrySet()) {
            assertEquals();
        }

         */
    }
}