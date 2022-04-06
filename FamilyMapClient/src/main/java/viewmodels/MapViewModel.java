package viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.familymapclient.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Set;

import model.Event;
import model.Person;

public class MapViewModel extends ViewModel {
    private static MapViewModel instance;

    private Event selectedEvent = null;
    private GoogleMap map;

    public static MapViewModel getInstance() {
        if (instance == null) {
            instance = new MapViewModel();
        }

        return instance;
    }

    private MapViewModel() {}

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void redrawMap(Person eventPerson) {
        //use this to select the correct marker
        //center the camera on the location of the event and redraw the lines
        LatLng latLng = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //setTextView(eventPerson);

        //createLines(selectedEvent);
    }
}
