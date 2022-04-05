package viewmodels;

import androidx.lifecycle.ViewModel;

public class SettingsActivityViewModel extends ViewModel {
    private static SettingsActivityViewModel instance;

    private boolean lifeLinesEnabled = true;
    private boolean familyTreeEnabled = true;
    private boolean spouseLinesEnabled = true;
    private boolean fatherSideEnabled = true;
    private boolean motherSideEnabled = true;
    private boolean maleEventsEnabled = true;
    private boolean femaleEventsEnabled = true;

    public static SettingsActivityViewModel getInstance() {
        if (instance == null) {
            instance = new SettingsActivityViewModel();
        }

        return instance;
    }

    private SettingsActivityViewModel() {}

    public boolean isLifeLinesEnabled() {
        return lifeLinesEnabled;
    }

    public void setLifeLinesEnabled(boolean lifeLinesEnabled) {
        this.lifeLinesEnabled = lifeLinesEnabled;
    }

    public boolean isFamilyTreeEnabled() {
        return familyTreeEnabled;
    }

    public void setFamilyTreeEnabled(boolean familyTreeEnabled) {
        this.familyTreeEnabled = familyTreeEnabled;
    }

    public boolean isSpouseLinesEnabled() {
        return spouseLinesEnabled;
    }

    public void setSpouseLinesEnabled(boolean spouseLinesEnabled) {
        this.spouseLinesEnabled = spouseLinesEnabled;
    }

    public boolean isFatherSideEnabled() {
        return fatherSideEnabled;
    }

    public void setFatherSideEnabled(boolean fatherSideEnabled) {
        this.fatherSideEnabled = fatherSideEnabled;
    }

    public boolean isMotherSideEnabled() {
        return motherSideEnabled;
    }

    public void setMotherSideEnabled(boolean motherSideEnabled) {
        this.motherSideEnabled = motherSideEnabled;
    }

    public boolean isMaleEventsEnabled() {
        return maleEventsEnabled;
    }

    public void setMaleEventsEnabled(boolean maleEventsEnabled) {
        this.maleEventsEnabled = maleEventsEnabled;
    }

    public boolean isFemaleEventsEnabled() {
        return femaleEventsEnabled;
    }

    public void setFemaleEventsEnabled(boolean femaleEventsEnabled) {
        this.femaleEventsEnabled = femaleEventsEnabled;
    }
}
