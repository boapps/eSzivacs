package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 24/09/17.
 */

public class TimetableLVAdapter extends BaseAdapter {
    LayoutInflater inflator = null;
    Context context;
    private ArrayList<Lesson> lessons;

    public TimetableLVAdapter(ArrayList<Lesson> lessons, Context con) {
        this.lessons = lessons;
        context = con;
        Themer.selectCurrentTheme(context);

        System.out.println("number: " + lessons.size());
    }

    @Override
    public int getCount() {
        return lessons.size();
    }

    @Override
    public Object getItem(int i) {
        return lessons.get(i).getSubjectName();
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
        if (!lessons.get(i).getState().equals("Missed"))
            subjectTV.setText(lessons.get(i).getSubject());
        else
            subjectTV.setText(lessons.get(i).getSubject() + " (Elmarad)");
        dateTV.setText(lessons.get(i).getRoom());
        valueTV.setText(String.valueOf(lessons.get(i).getCount()));

        return view;
    }

}
