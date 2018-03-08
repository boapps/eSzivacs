package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Datas.Absence;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 24/09/17.
 */

public class AbsencesLVAdapter extends BaseAdapter {
    LayoutInflater inflator = null;
    Context context;
    private ArrayList<Absence> absences;

    public AbsencesLVAdapter(ArrayList<Absence> absences, Context con) {
        this.absences = absences;
        context = con;
        Themer.selectCurrentTheme(context);
    }

    @Override
    public int getCount() {
        return absences.size();
    }

    @Override
    public Object getItem(int i) {
        return absences.get(i).getJustificationState();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            inflator = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.absence_lv_item, null);
        }
        TextView subjectTV = view.findViewById(R.id.subject_tv);
        TextView dateTV = view.findViewById(R.id.date_tv);
        TextView valueTV = view.findViewById(R.id.value_tv);
        subjectTV.setText(absences.get(i).getSubjectName());
        dateTV.setText(absences.get(i).getJustificationStateName());
        valueTV.setText(absences.get(i).getStartTime());

        switch (absences.get(i).getJustificationState()) {
            case "Justified":
                dateTV.setTextColor(Color.rgb(56, 142, 60));
                break;
            case "BeJustified":
                dateTV.setTextColor(Color.rgb(255, 87, 34));
                break;
            case "NotJustified":
                dateTV.setTextColor(Color.rgb(213, 0, 0));
                break;

        }
        return view;
    }
}
