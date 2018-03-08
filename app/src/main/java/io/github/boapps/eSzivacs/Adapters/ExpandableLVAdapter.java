package io.github.boapps.eSzivacs.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.boapps.eSzivacs.Datas.Absence;
import io.github.boapps.eSzivacs.Datas.AbsentDay;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 23/12/17.
 */

public class ExpandableLVAdapter extends BaseExpandableListAdapter {

    private final List<AbsentDay> itemList;
    private final LayoutInflater inflater;

    public ExpandableLVAdapter(Context context, List<AbsentDay> itemList) {
        this.inflater = LayoutInflater.from(context);
        this.itemList = itemList;
        Themer.selectCurrentTheme(context);
    }

    @Override
    public Absence getChild(int groupPosition, int childPosition) {

        return itemList.get(groupPosition).getAbsenceList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemList.get(groupPosition).getAbsenceList().size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.absence_lv_item, null); //TODO change layout id
        }

        final Absence item = getChild(groupPosition, childPosition);

//            holder.textLabel.setText(item.getStartTime());
        TextView subjectTV = convertView.findViewById(R.id.subject_tv);
        TextView dateTV = convertView.findViewById(R.id.date_tv);
        TextView valueTV = convertView.findViewById(R.id.value_tv);
        subjectTV.setText(item.getSubjectName());
        dateTV.setText(item.getJustificationStateName());
//            valueTV.setText(item.getStartTime());

        switch (item.getJustificationState()) {
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


        return convertView;
    }

    @Override
    public AbsentDay getGroup(int groupPosition) {
        return itemList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return itemList.size();
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View theConvertView, ViewGroup parent) {

        if (theConvertView == null) {
            theConvertView = inflater.inflate(R.layout.group_item, null); //TODO change layout id
        }
        final AbsentDay item = getGroup(groupPosition);

        TextView date = theConvertView.findViewById(R.id.date);
        date.setText(item.getAbsenceList().get(0).getStartTime() + (item.isJustified() ? " ✓" : ""));
        //            holder.textLabel.setText(item.getAbsenceList().get(0).getStartTime());

        return theConvertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static final class ViewHolder {
    }

}


