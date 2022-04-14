package viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.DataCache;
import model.Person;

public class PersonActivityViewModel extends ViewModel {
    private final Map<String, String> immediateFamilyMap = new HashMap<>();
    private final DataCache dataCache = DataCache.getInstance();
    Map<String, Person> personById = dataCache.getPersonById();

    public Map<String, String> getImmediateFamilyMap() {
        return immediateFamilyMap;
    }

    public List<Person> getImmediateFamily(String personID) {
        List<Person> immediateFamily = new ArrayList<>();
        Person person = personById.get(personID);

        String fatherID = person.getFatherID();
        String motherID = person.getMotherID();
        String spouseID = person.getSpouseID();
        Person child = getChild(person);

        String fatherRelation = "Father";
        String motherRelation = "Mother";
        String spouseRelation = "Spouse";
        String childRelation = "Child";

        boolean isFatherMotherNull = isFatherMotherNull(fatherID, motherID);
        boolean isSpouseNull = isSpouseNull(spouseID);

        if (!isFatherMotherNull && child != null && !isSpouseNull) {
            immediateFamily.add(personById.get(fatherID));
            addToImmediateFamilyMap(fatherID, fatherRelation);

            immediateFamily.add(personById.get(motherID));
            addToImmediateFamilyMap(motherID, motherRelation);

            immediateFamily.add(personById.get(spouseID));
            addToImmediateFamilyMap(spouseID, spouseRelation);

            immediateFamily.add(child);
            addToImmediateFamilyMap(child.getPersonID(), childRelation);
        }
        else if (isFatherMotherNull && child != null && !isSpouseNull) {
            immediateFamily.add(personById.get(spouseID));
            addToImmediateFamilyMap(spouseID, spouseRelation);

            immediateFamily.add(child);
            addToImmediateFamilyMap(child.getPersonID(), childRelation);
        }
        else if (isFatherMotherNull && child == null && !isSpouseNull) {
            immediateFamily.add(personById.get(spouseID));
            addToImmediateFamilyMap(spouseID, spouseRelation);
        }
        else if (child == null && !isFatherMotherNull && !isSpouseNull) {
            immediateFamily.add(personById.get(fatherID));
            addToImmediateFamilyMap(fatherID, fatherRelation);

            immediateFamily.add(personById.get(motherID));
            addToImmediateFamilyMap(motherID, motherRelation);

            immediateFamily.add(personById.get(spouseID));
            addToImmediateFamilyMap(spouseID, spouseRelation);
        }
        else if (isFatherMotherNull && isSpouseNull && child != null) {
            immediateFamily.add(getChild(person));
            addToImmediateFamilyMap(child.getPersonID(), childRelation);
        }
        else if (child == null && isSpouseNull && !isFatherMotherNull) {
            immediateFamily.add(personById.get(fatherID));
            addToImmediateFamilyMap(fatherID, fatherRelation);

            immediateFamily.add(personById.get(motherID));
            addToImmediateFamilyMap(motherID, motherRelation);
        }
        else if (isSpouseNull && child != null && !isFatherMotherNull) {
            immediateFamily.add(personById.get(fatherID));
            addToImmediateFamilyMap(fatherID, fatherRelation);

            immediateFamily.add(personById.get(motherID));
            addToImmediateFamilyMap(motherID, motherRelation);

            immediateFamily.add(child);
            addToImmediateFamilyMap(child.getPersonID(), childRelation);
        }

        return immediateFamily;
    }

    private boolean isFatherMotherNull(String fatherID, String motherID) {
        return (fatherID == null || fatherID.equals("")) && (motherID == null || motherID.equals(""));
    }

    private boolean isSpouseNull(String spouseID) {
        return spouseID == null || spouseID.equals("");
    }

    private Person getChild(Person person) {
        for (Map.Entry<String, Person> personEntry : personById.entrySet()) {
            if (personEntry.getValue().getMotherID().equals(person.getPersonID()) || personEntry.getValue().getFatherID().equals(person.getPersonID())) {
                return personEntry.getValue();
            }
        }

        return null;
    }

    private void addToImmediateFamilyMap(String personID, String relationship) {
        immediateFamilyMap.put(personID, relationship);
    }
}
