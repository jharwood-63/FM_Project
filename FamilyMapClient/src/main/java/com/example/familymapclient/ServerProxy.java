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
import result.LoginResult;
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

    public LoginResult doPost(URL url, UserRequest userRequest) throws IOException {
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

            LoginResult result = (LoginResult) getResult(connection);

            return result;
        }
        catch (IOException e) {
            throw new IOException("Error: unable to perform post");
        }

    }

    private LoginResult getResult(HttpURLConnection connection) throws IOException {
        Gson gson = new Gson();
        InputStream inputStream = connection.getInputStream();
        Reader respBody = new InputStreamReader(inputStream);
        return gson.fromJson(respBody, LoginResult.class);
    }
}
