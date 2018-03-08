package io.github.boapps.eSzivacs.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.Datas.Week;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.DataLoader;

/**
 * Created by boa on 21/10/17.
 */

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public static Week ttweek;
    public static ArrayList<Lesson> lessons;
    ArrayList<Lesson> lessonsc = new ArrayList<>();
    private Context context = null;
    private int appWidgetId;
    private SharedPreferences sharedPreferences;
    private String usr;
    private String psw;
    private String schoolCode;
    private String schoolUrl;


    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d("AppWidgetId", String.valueOf(appWidgetId));
//        dbhelper = new DBHelper(this.context);
    }

    private void updateWidgetListView() {
        sharedPreferences = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        usr = sharedPreferences.getString("usernames", " ");
        usr = usr.replace("--", "");
        sharedPreferences = context.getSharedPreferences(usr, Activity.MODE_PRIVATE);
        psw = sharedPreferences.getString("pw", "");
        schoolCode = sharedPreferences.getString("schoolCode", "");
        schoolUrl = sharedPreferences.getString("schoolUrl", "");

        DataLoader dloader = new DataLoader(context);
        dloader.setLogin(usr, psw, schoolUrl, schoolCode);
        try {
            dloader.doLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date from = new Date();
        Date to = new Date();
        from.setDate(from.getDate() - from.getDay() + 1);
        to.setDate(from.getDate() + 6);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        lessons = new ArrayList<>();
        try {
            lessons = dloader.getTimetable(simpleDateFormat.format(from), simpleDateFormat.format(to), dloader.getBearerCode());
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
        Date d = new Date();
        System.out.println(d.getDay());
        int day = 1;
        if (d.getDay() < 6)
            day = d.getDay();

        lessonsc.clear();
        for (Lesson lesson : lessons) {
            if (lesson.getDate().getDay() == day) {
                lessonsc.add(lesson);
            }
        }

        ttweek = new Week(from, lessonsm, lessonst, lessonsw, lessonsth, lessonsf);


    }

    @Override
    public int getCount() {
        return lessonsc.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.main_lv_item);
        remoteView.setTextViewText(R.id.value_tv, String.valueOf(lessonsc.get(position).getCount()));
        remoteView.setTextViewText(R.id.subject_tv, lessonsc.get(position).getSubjectName());
        remoteView.setTextViewText(R.id.date_tv, lessonsc.get(position).getRoom());

        return remoteView;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        lessonsc.clear();
//        dbhelper.close();
    }
}
