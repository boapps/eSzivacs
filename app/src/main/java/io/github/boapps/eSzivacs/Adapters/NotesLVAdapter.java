package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Datas.Note;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 24/09/17.
 */

public class NotesLVAdapter extends BaseAdapter {
    LayoutInflater inflator = null;
    Context context;
    private ArrayList<Note> notes;

    public NotesLVAdapter(ArrayList<Note> notes, Context con) {
        this.notes = notes;
        context = con;
        Themer.selectCurrentTheme(context);
    }

//    @Override
//    public boolean areAllItemsEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled(int i) {
//        return true;
//    }

//    @Override
//    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
//
//    }
//
//    @Override
//    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
//
//    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int i) {
        return notes.get(i).getContent();
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
        subjectTV.setText(notes.get(i).getContent());
        dateTV.setText(notes.get(i).getDate());
        if (valueTV != null) {
            valueTV.setEnabled(false);
            ((LinearLayout) valueTV.getParent()).removeView(valueTV);
        }
        return view;
    }

}
