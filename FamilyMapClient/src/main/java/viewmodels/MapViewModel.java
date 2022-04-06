package viewmodels;

import androidx.lifecycle.ViewModel;

import model.Event;

public class MapViewModel extends ViewModel {
    private static MapViewModel instance;

    public static MapViewModel getInstance() {
        if (instance == null) {
            instance = new MapViewModel();
        }

        return instance;
    }

    private MapViewModel() {}

    private Event selectedEvent = null;

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
}
