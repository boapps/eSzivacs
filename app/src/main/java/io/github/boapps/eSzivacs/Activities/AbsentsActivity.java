package io.github.boapps.eSzivacs.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.boapps.eSzivacs.Adapters.ExpandableLVAdapter;
import io.github.boapps.eSzivacs.Datas.Absence;
import io.github.boapps.eSzivacs.Datas.AbsentDay;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.absences;

public class AbsentsActivity extends AppCompatActivity {
    //Here is information about when, why and where was the student absent and whether it was justified.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        setContentView(R.layout.activity_absents);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ExpandableListView absents_lv = findViewById(R.id.absents_lv);
        List<AbsentDay> itemList = new ArrayList<AbsentDay>();
        ArrayList<Absence> tempAbsences = new ArrayList<>(absences);

        Collections.sort(tempAbsences, new Comparator<Absence>() {
            @Override
            public int compare(Absence o1, Absence o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        String tempDay = "";
        AbsentDay parent;

        for (int n = 0; n < tempAbsences.size(); n++) {
            if (tempDay.equals(tempAbsences.get(n).getStartTime()))
                parent = itemList.get(itemList.size() - 1);
            else
                parent = new AbsentDay();
            parent.getAbsenceList().add(tempAbsences.get(n));
            if (!tempDay.equals(tempAbsences.get(n).getStartTime()))
                itemList.add(parent);
            tempDay = tempAbsences.get(n).getStartTime();
        }

        ExpandableLVAdapter adapter = new ExpandableLVAdapter(this, itemList);
        absents_lv.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
