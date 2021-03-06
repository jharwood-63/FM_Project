package result;

/**
 * FillResponse object holds data for a response to a fill operation
 */

public class FillResult extends Result {
    private int numPersons;
    private int numEvents;

    /**
     * FillResponse constructor for creating a response to a fill operation. The message is different based on the success status.
     * @param success Status of the operation
     * @param numEvents Number of events added to the database
     * @param numPersons Number of persons added to the database
     */

    public FillResult(int numPersons, int numEvents, boolean success) {
        super("Successfully added " + numPersons + " persons and " + numEvents + " events to the database", success);
        this.numPersons = numPersons;
        this.numEvents = numEvents;
    }

    public int getNumPersons() {
        return numPersons;
    }

    public int getNumEvents() {
        return numEvents;
    }
}
