package io.github.boapps.eSzivacs.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.github.boapps.eSzivacs.Adapters.TimetableLVAdapter;
import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.Datas.Week;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.ttweek;

public class Timetable extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    Date selectedDate = new Date();
    private Button selectWeekBtn;
    private ImageButton nextWeekBtn;
    private ImageButton prevWeekBtn;

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

        selectWeekBtn = findViewById(R.id.selectWeekBtn);
        nextWeekBtn = findViewById(R.id.nextWeekBtn);
        prevWeekBtn = findViewById(R.id.prevWeekBtn);
        selectWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDialog();
            }
        });
        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarNext = Calendar.getInstance();
                calendarNext.setTime(selectedDate);
                calendarNext.add(Calendar.DATE, 7);
                selectedDate = calendarNext.getTime();
                new Thread(new Runnable() {
                    public void run() {
                        loadNewWeek();
                    }
                }).start();
            }
        });
        prevWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarNext = Calendar.getInstance();
                calendarNext.setTime(selectedDate);
                calendarNext.add(Calendar.DATE, -7);
                selectedDate = calendarNext.getTime();
                new Thread(new Runnable() {
                    public void run() {
                        loadNewWeek();
                    }
                }).start();
            }
        });
    }

    public int convertDayToNonRetard(int day) {
        return day == 1 ? 7 : day - 1;
    }

    public void loadNewWeek() {

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(selectedDate);
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(selectedDate);

        System.out.println("CALENDAR:");
        calendarFrom.set(Calendar.DAY_OF_WEEK, 2); //THIS IS F* MONDAY
        calendarTo.set(Calendar.DAY_OF_WEEK, 1); //THIS IS F* SUNDAY

        System.out.println(convertDayToNonRetard(calendarFrom.get(Calendar.DAY_OF_WEEK)));
        System.out.println(convertDayToNonRetard(calendarTo.get(Calendar.DAY_OF_WEEK)));

/*
        Date from = new Date();
        Date to = selectedDate;
        System.out.println("TIME: ");
        System.out.println(from.getTime());

        from.setTime(from.getTime());
        from.setDate(from.getDate() - from.getDay() + 1);
        to.setYear(from.getYear());
        to.setMonth(from.getMonth());
        to.setDate(from.getDate() + 6);
        */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<Lesson> lessons = new ArrayList<>();
        lessons.clear();

        System.out.println(calendarFrom.getTime().toString());
        System.out.println(calendarTo.getTime().toString());
        System.out.println("hello");

        try {
            lessons = MainPage.dloader.getTimetable(simpleDateFormat.format(calendarFrom.getTime()), simpleDateFormat.format(calendarTo.getTime()), MainPage.dloader.getBearerCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Lesson> lessonsm = new ArrayList<>();
        ArrayList<Lesson> lessonst = new ArrayList<>();
        ArrayList<Lesson> lessonsw = new ArrayList<>();
        ArrayList<Lesson> lessonsth = new ArrayList<>();
        ArrayList<Lesson> lessonsf = new ArrayList<>();

        for (Lesson lesson : lessons) {
            switch (lesson.getDate().getDay()) {
                case 1:
                    lessonsm.add(lesson);
                    break;
                case 2:
                    lessonst.add(lesson);
                    break;
                case 3:
                    lessonsw.add(lesson);
                    break;
                case 4:
                    lessonsth.add(lesson);
                    break;
                case 5:
                    lessonsf.add(lesson);
                    break;
            }
        }

        ttweek = new Week(calendarFrom.getTime(), lessonsm, lessonst, lessonsw, lessonsth, lessonsf);

//        mViewPager.notifyAll();
        mViewPager.post(new Runnable() {
            public void run() {
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

    }

    public void SelectDialog() {

        Dialog selectDialogView;
        LayoutInflater li = LayoutInflater.from(this);
        View selDialog = li.inflate(R.layout.select_week_dialog,
                null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(selDialog);
        alertDialogBuilder.setPositiveButton("kiválaszt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    public void run() {
                        loadNewWeek();
                    }
                }).start();

            }
        });
        selectDialogView = alertDialogBuilder.create();
        selectDialogView.setTitle("Válassz egy hetet");
        selectDialogView.show();

        CalendarView calendarView = selectDialogView.findViewById(R.id.calendarView);
        calendarView.setSelectedWeekBackgroundColor(Color.BLACK);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int selectedYear,
                                            int selectedMonth, int selectedDay) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy. MM. dd. ");
                try {
                    selectedDate = format.parse(selectedYear + ". " + (selectedMonth + 1) + ". " + selectedDay + ". ");
                    selectWeekBtn.setText(selectedYear + ". " + (selectedMonth + 1) + ". " + selectedDay + ". ");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
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
