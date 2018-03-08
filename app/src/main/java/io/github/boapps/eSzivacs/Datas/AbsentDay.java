package io.github.boapps.eSzivacs.Datas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boa on 23/12/17.
 */

public class AbsentDay {
    private List<Absence> absenceList;

    public AbsentDay() {
        absenceList = new ArrayList<Absence>();
    }

    public List<Absence> getAbsenceList() {
        return absenceList;
    }

    public boolean isJustified() {
        for (Absence absence : absenceList)
            if (!absence.getJustificationState().equals("Justified"))
                return false;
        return true;
    }
}
