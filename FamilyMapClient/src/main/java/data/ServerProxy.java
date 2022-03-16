package data;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

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

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                InputStream inputStream = connection.getInputStream();
                Reader respBody = new InputStreamReader(inputStream);
                return gson.fromJson(respBody, Result.class);
            }
            else {
                //FIXME: NOT SURE WHAT TO DO HERE YET
                //if it gets here that means the success status is false so you should print out the message that was returned
                throw new IOException();
            }
        }
        catch (IOException e) {
            throw new IOException("Error: unable to fill id maps");
        }
    }
}
