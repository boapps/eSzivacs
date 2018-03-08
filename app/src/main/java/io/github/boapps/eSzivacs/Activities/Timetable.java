package io.github.boapps.eSzivacs.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.boapps.eSzivacs.Adapters.TimetableLVAdapter;
import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.ttweek;

public class Timetable extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);

        setContentView(R.layout.activity_timetable);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        int showDay = new Date().getDay();
        if (showDay <= 5)
            mViewPager.setCurrentItem(showDay - 1);
        else
            mViewPager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Dialog dialog;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void timetableDialog(final Lesson lesson) {
            LayoutInflater li = LayoutInflater.from(getContext());
            View evsDialog = li.inflate(R.layout.timetable_dialog,
                    null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setView(evsDialog);
            alertDialogBuilder.setNegativeButton(R.string.title_menu_share, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, lesson.getSubjectName() + " - " + lesson.getStateName());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            });
            dialog = alertDialogBuilder.create();
//            dialog.setTitle(getString(R.string.title_activity_lessons_list));
            dialog.show();

            TextView lessonTX = dialog.findViewById(R.id.lessonTX);
            lessonTX.setText(lesson.getSubjectName());

            TextView roomTX = dialog.findViewById(R.id.roomTX);
            roomTX.setText(lesson.getRoom());

            TextView teacherTX = dialog.findViewById(R.id.teacherTX);
            System.out.println(lesson.getDepTeacher());
            System.out.println(lesson.getTeacher());
            teacherTX.setText(lesson.getDepTeacher().equals("") ? lesson.getTeacher() : lesson.getTeacher() + " -> " + lesson.getDepTeacher());

            TextView timeTX = dialog.findViewById(R.id.timeTX);
            SimpleDateFormat format = new SimpleDateFormat("kk:mm");
            timeTX.setText(format.format(lesson.getStart()) + "-" + format.format(lesson.getEnd()));

            TextView themeTX = dialog.findViewById(R.id.themeTX);
            themeTX.setText(lesson.getTheme());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
            ListView listView = rootView.findViewById(R.id.dayLV);
            TextView textView2 = rootView.findViewById(R.id.dayTV);

            TimetableLVAdapter fAdapter = new TimetableLVAdapter(ttweek.getFriday(), getContext());

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    listView.setAdapter(new TimetableLVAdapter(ttweek.getMonday(), getContext()));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                            timetableDialog(ttweek.getMonday().get(position));
                        }
                    });
                    break;
                case 2:
                    listView.setAdapter(new TimetableLVAdapter(ttweek.getTuesday(), getContext()));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                            timetableDialog(ttweek.getTuesday().get(position));
                        }
                    });
                    break;
                case 3:
                    listView.setAdapter(new TimetableLVAdapter(ttweek.getWednesday(), getContext()));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                            timetableDialog(ttweek.getWednesday().get(position));
                        }
                    });
                    break;
                case 4:
                    listView.setAdapter(new TimetableLVAdapter(ttweek.getThursday(), getContext()));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                            timetableDialog(ttweek.getThursday().get(position));
                        }
                    });
                    break;
                case 5:
                    listView.setAdapter(fAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                            timetableDialog(ttweek.getFriday().get(position));
                        }
                    });
                    break;
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy. MM. dd. ");
            Calendar c = Calendar.getInstance();
            c.setTime(ttweek.getStartday());
            c.add(Calendar.DATE, getArguments().getInt(ARG_SECTION_NUMBER) - 1);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    textView2.setText(getString(R.string.day_mon) + " (" + format.format(c.getTime()) + ")");
                    break;
                case 2:
                    textView2.setText(getString(R.string.day_tue) + " (" + format.format(c.getTime()) + ")");
                    break;
                case 3:
                    textView2.setText(getString(R.string.day_wed) + " (" + format.format(c.getTime()) + ")");
                    break;
                case 4:
                    textView2.setText(getString(R.string.day_thu) + " (" + format.format(c.getTime()) + ")");
                    break;
                case 5:
                    textView2.setText(getString(R.string.day_fri) + " (" + format.format(c.getTime()) + ")");
                    break;
            }

            System.out.println(ttweek.getFriday().size());
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
