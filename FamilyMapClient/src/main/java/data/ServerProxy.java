package data;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

            connection.addRequestProperty("Accept", "application/json");

            connection.connect();

            OutputStreamWriter requestBody = new OutputStreamWriter(connection.getOutputStream());
            Gson gson = new Gson();
            gson.toJson(userRequest, requestBody);
            requestBody.close();

            return getResult(connection, "login");
        }
        catch (IOException e) {
            throw new IOException("Error: unable to perform post");
        }

    }

    private Result getResult(HttpURLConnection connection, String resultType) throws IOException {
        Gson gson = new Gson();
        Reader respBody;

        Result result = null;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            respBody = new InputStreamReader(connection.getInputStream());

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
        }
        else {
            respBody = new InputStreamReader(connection.getErrorStream());

            result = (Result) gson.fromJson(respBody, Result.class);
        }

        return result;
    }
}
