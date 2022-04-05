package viewmodels;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private boolean isLoggedIn;

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
