package viewmodels;

import model.Event;

public class MapViewModel {
    private static MapViewModel instance;

    private Event selectedEvent = null;

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
}
