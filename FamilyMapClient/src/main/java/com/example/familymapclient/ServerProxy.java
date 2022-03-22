package com.example.familymapclient;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import requests.UserRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.Result;

public class ServerProxy {
    public Result doGet(String urlString, String authToken) throws IOException {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.addRequestProperty("Authorization", authToken);
            connection.setRequestMethod("GET");

            Result result = null;
            if (urlString.contains("person")) {
                result = (PersonResult) getResult(connection, "person");
            }
            else if (urlString.contains("event")) {
                result = (EventResult) getResult(connection, "event");
            }

            return result;
        }
        catch (IOException e) {
            throw new IOException("Error: unable to get data from database");
        }
    }

    public Result doPost(URL url, UserRequest userRequest) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.connect();

            try (OutputStream requestBody = connection.getOutputStream()) {
                DataOutputStream dos = new DataOutputStream(requestBody);
                Gson gson = new Gson();
                String requestString = gson.toJson(userRequest);

                dos.writeBytes(requestString);
                dos.flush();
            }

            Result result = (LoginResult) getResult(connection, "login");

            return result;
        }
        catch (IOException e) {
            throw new IOException("Error: unable to perform post");
        }

    }

    private Result getResult(HttpURLConnection connection, String resultType) throws IOException {
        Gson gson = new Gson();
        InputStream inputStream = connection.getInputStream();
        Reader respBody = new InputStreamReader(inputStream);

        Result result = null;

        switch (resultType) {
            case "login":
                result = (LoginResult) gson.fromJson(respBody, LoginResult.class);
                break;
            case "person":
                result = (PersonResult) gson.fromJson(respBody, PersonResult.class);
                break;
            case "event":
                result = (EventResult) gson.fromJson(respBody, EventResult.class);
                break;
        }

        return result;
    }
}
