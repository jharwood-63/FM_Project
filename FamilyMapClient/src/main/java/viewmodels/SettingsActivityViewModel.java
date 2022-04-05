package viewmodels;

import androidx.lifecycle.ViewModel;

public class SettingsActivityViewModel extends ViewModel {
    private boolean lifeLinesEnabled;
    private boolean familyTreeEnabled;
    private boolean spouseLinesEnabled;
    private boolean fatherSideEnabled;
    private boolean motherSideEnabled;
    private boolean maleEventsEnabled;
    private boolean femaleEventsEnabled;

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
