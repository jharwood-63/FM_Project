package com.example.familymapclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import requests.LoginRequest;
import requests.RegisterRequest;
import requests.UserRequest;
import result.Result;

public class ServerProxy {
    public Result doGet(String urlString, String authToken) throws IOException {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.addRequestProperty("Authorization", authToken);
            connection.connect();

            return getResult(connection);
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

            connection.addRequestProperty("username", userRequest.getUsername());
            connection.addRequestProperty("password", userRequest.getPassword());

            //check if it is a register request and then add those attributes
            if (url.toString().contains("register")) {
                RegisterRequest registerRequest = (RegisterRequest) userRequest;
                connection.addRequestProperty("email", registerRequest.getEmail());
                connection.addRequestProperty("firstName", registerRequest.getFirstName());
                connection.addRequestProperty("lastName", registerRequest.getLastName());
                connection.addRequestProperty("gender", registerRequest.getGender());
            }

            connection.connect();

            return getResult(connection);
        }
        catch (IOException e) {
            throw new IOException("Error: unable to perform post");
        }

    }

    private Result getResult(HttpURLConnection connection) throws IOException {
        Gson gson = new Gson();
        InputStream inputStream = connection.getInputStream();
        Reader respBody = new InputStreamReader(inputStream);
        return gson.fromJson(respBody, Result.class);
    }
}
