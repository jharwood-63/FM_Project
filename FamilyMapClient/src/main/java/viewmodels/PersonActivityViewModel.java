package viewmodels;

import androidx.lifecycle.ViewModel;

public class PersonActivityViewModel extends ViewModel {
    private String personID;

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
