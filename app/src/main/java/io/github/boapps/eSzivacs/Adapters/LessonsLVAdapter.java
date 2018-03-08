package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Datas.Subject;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 24/09/17.
 */

public class LessonsLVAdapter extends BaseAdapter {
    LayoutInflater inflator = null;
    Context context;
    private ArrayList<Subject> subjects;

    public LessonsLVAdapter(ArrayList<Subject> subjects, Context con) {
        this.subjects = subjects;
        context = con;
        Themer.selectCurrentTheme(context);
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Override
    public Object getItem(int i) {
        return subjects.get(i).getValue();
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
            view = inflator.inflate(R.layout.main_lv_item, null);
        }
        TextView subjectTV = view.findViewById(R.id.subject_tv);
        TextView dateTV = view.findViewById(R.id.date_tv);
        TextView valueTV = view.findViewById(R.id.value_tv);
        subjectTV.setText(subjects.get(i).getSubject());
        dateTV.setText(String.valueOf(subjects.get(i).getDiff()));
        valueTV.setText(String.valueOf(subjects.get(i).getValue()));

        return view;
    }
}
