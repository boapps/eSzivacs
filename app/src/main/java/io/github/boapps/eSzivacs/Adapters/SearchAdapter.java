package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Datas.School;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 29/09/17.
 */

public class SearchAdapter extends BaseAdapter {

    private ArrayList<School> schools;

    private String whatToDisable;
    private LayoutInflater inflator = null;
    private Context context;

    public SearchAdapter(ArrayList<School> schools, Context con, String whatToDisable) {
        this.schools = schools;
        context = con;
        this.whatToDisable = whatToDisable;
        Themer.selectCurrentTheme(context);
    }

    @Override
    public boolean isEnabled(int i) {
        return (schools.get(i).getName().toLowerCase().contains(whatToDisable.toLowerCase()));

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        return schools.size();
    }

    @Override
    public Object getItem(int i) {
        return schools.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        boolean convertViewWasNull = false;

        if (view == null) {
            inflator = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);


            view = inflator.inflate(R.layout.school_lv_item, null);

        }
        TextView schoolNameTV = view.findViewById(R.id.school_name_tv);
        schoolNameTV.setText(getItem(i).toString());
        view.setTag(schools.get(i));
        return view;
    }


}
