package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 24/09/17.
 */

public class EditLVAdapter extends BaseAdapter {
    LayoutInflater inflator = null;
    Context context;
    private ArrayList<Evaluation> evaluationsb;

    public EditLVAdapter(ArrayList<Evaluation> evaluationsb, Context con) {
        System.out.println("evsize=" + evaluationsb.size());
        this.evaluationsb = evaluationsb;
        context = con;
        Themer.selectCurrentTheme(context);
    }

    @Override
    public int getCount() {
        return evaluationsb.size();
    }

    @Override
    public Object getItem(int i) {
        return evaluationsb.get(i).getValue();
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
        try {
            subjectTV.setText(evaluationsb.get(i).getSubject());
            dateTV.setText(evaluationsb.get(i).getDate());
            valueTV.setText(evaluationsb.get(i).getNumericValue());

            try {
                if (Integer.valueOf(evaluationsb.get(i).getWeight().replace("%", "")) > 100)
                    valueTV.setTextColor(Themer.getTextColor(context));
                else
                    valueTV.setTextColor(Themer.getTextColor(context));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return view;
    }
}
