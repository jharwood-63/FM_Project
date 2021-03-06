package model;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Deserializer {
    public LocationData deserializeLocation() throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("ServerResources/json/locations.json")){
            LocationData locationData = (LocationData) gson.fromJson(reader, LocationData.class);
            return locationData;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Location file not found");
        }
    }

    public NameData deserializeNames(String fileName) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(fileName)){
            NameData nameData = (NameData) gson.fromJson(reader, NameData.class);
            return nameData;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Name file not found");
        }
    }
}
