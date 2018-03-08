package io.github.boapps.eSzivacs.Activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.boapps.eSzivacs.Adapters.MainLVAdapter;
import io.github.boapps.eSzivacs.Datas.Absence;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.Datas.Event;
import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.Datas.Note;
import io.github.boapps.eSzivacs.Datas.Student;
import io.github.boapps.eSzivacs.Datas.Subject;
import io.github.boapps.eSzivacs.Datas.Week;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.AccountManager;
import io.github.boapps.eSzivacs.Utils.BackgroundTaskService;
import io.github.boapps.eSzivacs.Utils.DataLoader;
import io.github.boapps.eSzivacs.Utils.Themer;

import static java.lang.Thread.sleep;


public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<Lesson> lessons = new ArrayList<>();
    public static Week ttweek = new Week(new Date(), new ArrayList<Lesson>(), new ArrayList<Lesson>(), new ArrayList<Lesson>(), new ArrayList<Lesson>(), new ArrayList<Lesson>());
    public static ArrayList<Evaluation> evaluations = new ArrayList<>();
    public static ArrayList<Evaluation> evaluationsfelevi = new ArrayList<>();
    public static ArrayList<Evaluation> evaluationsevvegi = new ArrayList<>();
    public static ArrayList<Event> events = new ArrayList<>();
    public static ArrayList<Note> notes = new ArrayList<>();
    public static ArrayList<Absence> absences = new ArrayList<>();
    public static ArrayList<Subject> subjects = new ArrayList<>();
    public static Student user;
    private SharedPreferences sharedPreferences;
    private String usr;
    private String psw;
    private String schoolCode;
    private String schoolUrl;
    private ListView mainEvalList;
    private Dialog dialog;
    private Date today;
    private TextView nextLessonInfo;
    private ProgressBar loadProgress;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);

        setContentView(R.layout.activity_main_page);
//        super.onCreateDrawer();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean analitics = sharedPref.getBoolean(SettingsActivity.ANALITICS_SWITCH, true);
        FirebaseCrash.setCrashCollectionEnabled(analitics);
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(analitics);

        if (this.getSharedPreferences("prefs", Activity.MODE_PRIVATE).getBoolean("analitics_dialog", true)) {
            final Dialog analitDialog = new Dialog(this);
            analitDialog.setContentView(R.layout.analitics_dialog);
            analitDialog.setCancelable(false);
            analitDialog.setTitle("Figyelem");
            Button acceptBtn = analitDialog.findViewById(R.id.accept_btn);
            WebView webview = analitDialog.findViewById(R.id.policy_webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("file:///android_asset/privacy_policy.html");

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getApplication().getSharedPreferences("prefs", Activity.MODE_PRIVATE).edit().putBoolean("needNotified", false).apply();
                    analitDialog.dismiss();
                }
            });
            this.getSharedPreferences("prefs", Activity.MODE_PRIVATE).edit().putBoolean("analitics_dialog", false).apply();
            analitDialog.show();

        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //if no user is registered

        loadProgress = findViewById(R.id.loadProgress);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "openID");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "open");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "app");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);


        if (this.getSharedPreferences("prefs", Activity.MODE_PRIVATE).getString("usernames", "").equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {


            nextLessonInfo = findViewById(R.id.next_lesson_info);

            Boolean sync = sharedPref.getBoolean(SettingsActivity.NOTIFICATION_SWITCH, true);

            if (sync)
                BackgroundTaskService.scheduleRepeat(this);
            else
                BackgroundTaskService.cancelRepeat(this);


            //load needed data
            new Thread(new Runnable() {
                public void run() {
                    loadAllThings();
                }
            }).start();
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            Button moreBtn = findViewById(R.id.more_btn);

            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EvaluationListActivity.class);
                    startActivity(intent);
                }
            });

            final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshMain);
            refreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new Thread(new Runnable() {
                                public void run() {
                                    loadAllThings();
                                    refreshLayout.post(new Runnable() {
                                        public void run() {
                                            refreshLayout.setRefreshing(false);
                                        }
                                    });

                                }
                            }).start();

                        }
                    }
            );


        }

    }

    public void EvaluationsDialog(final Evaluation evaluation) {
        if (dialog == null) {
            LayoutInflater li = LayoutInflater.from(this);
            View evsDialog = li.inflate(R.layout.evs_dialog,
                    null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setView(evsDialog);
            alertDialogBuilder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, evaluation.getSubject() + " - " + evaluation.getNumericValue());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            });
            dialog = alertDialogBuilder.create();
//            dialog.setTitle("Evaluation");
            dialog.show();
            TextView valueTX = dialog.findViewById(R.id.valueTX);
            valueTX.setText(evaluation.getNumericValue() + (evaluation.getWeight().replace("%", "").equals("100") ? "" : "(X" + (Integer.valueOf(evaluation.getWeight().replace("%", ""))) / 100 + ")"));

            TextView subjectTX = dialog.findViewById(R.id.subjectTX);
            subjectTX.setText(evaluation.getSubject());

            TextView themeTX = dialog.findViewById(R.id.themeTX);
            themeTX.setText(evaluation.getTheme());

            TextView teacherTX = dialog.findViewById(R.id.teacherTX);
            teacherTX.setText(evaluation.getTeacher());

            TextView dateTX = dialog.findViewById(R.id.dateTX);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(evaluation.getDate().replace("T00:00:00", ""));
                System.out.println("Date ->" + date);
                format.applyLocalizedPattern("yyyy. MM. dd.");
                dateTX.setText(format.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
//                dateTX.setText(evaluation.getDate());
            }

        } else if (!dialog.isShowing()) {

            LayoutInflater li = LayoutInflater.from(this);
            View evsDialog = li.inflate(R.layout.evs_dialog,
                    null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setView(evsDialog);
            alertDialogBuilder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, evaluation.getSubject() + " - " + evaluation.getNumericValue());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            });
            dialog = alertDialogBuilder.create();
//            dialog.setTitle("Evaluation");
            dialog.show();
            TextView valueTX = dialog.findViewById(R.id.valueTX);
            valueTX.setText(evaluation.getNumericValue() + (evaluation.getWeight().replace("%", "").equals("100") ? "" : "(X" + (Integer.valueOf(evaluation.getWeight().replace("%", ""))) / 100 + ")"));

            TextView subjectTX = dialog.findViewById(R.id.subjectTX);
            subjectTX.setText(evaluation.getSubject());

            TextView themeTX = dialog.findViewById(R.id.themeTX);
            themeTX.setText(evaluation.getTheme());

            TextView teacherTX = dialog.findViewById(R.id.teacherTX);
            teacherTX.setText(evaluation.getTeacher());

            TextView dateTX = dialog.findViewById(R.id.dateTX);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(evaluation.getDate().replace("T00:00:00", ""));
                System.out.println("Date ->" + date);
                format.applyLocalizedPattern("yyyy. MM. dd.");
                dateTX.setText(format.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
//                dateTX.setText(evaluation.getDate());
            }
        }
    }

    public void progressTo(final int value) {
        loadProgress.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(loadProgress, "progress", loadProgress.getProgress(), value);
                progressAnimator.setDuration(200);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main) {
            Intent intent = new Intent(this, EvaluationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.student) {
            Intent intent = new Intent(this, StudentActivity.class);
            startActivity(intent);
        } else if (id == R.id.timetable) {
            Intent intent = new Intent(this, Timetable.class);
            startActivity(intent);
        } else if (id == R.id.absent) {
            Intent intent = new Intent(this, AbsentsActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.delete_kreta) {
            Uri packageURI = Uri.parse("package:"+"hu.eKreta.KretaAndroid");
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
        }*/ else if (id == R.id.events) {
            Intent intent = new Intent(this, EventActivity.class);
            startActivity(intent);
        } else if (id == R.id.accounts) {
            Intent intent = new Intent(this, AccountsActivity.class);
            startActivity(intent);
        } else if (id == R.id.notes) {
            Intent intent = new Intent(this, NotesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "e-Szivacs");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/ \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
            }
        } else if (id == R.id.nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "eszivacs@gmail.com", null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "V");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.changelog) {
            Intent intent = new Intent(this, ChangelogActivity.class);
            startActivity(intent);
        } else if (id == R.id.milenneha) {
            Intent intent = new Intent(this, MilennehaSelect.class);
            startActivity(intent);
        } else if (id == R.id.lessons) {
            Intent intent = new Intent(this, LessonsListActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadNotifications() {

    }

    public void loadAllThings() {
        //load user details
        sharedPreferences = getApplication().getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        AccountManager accountManager = new AccountManager(getApplicationContext());
        usr = accountManager.getSelectedAccount();
        sharedPreferences = getApplication().getSharedPreferences(usr, Activity.MODE_PRIVATE);
        psw = sharedPreferences.getString("pw", "");
        schoolCode = sharedPreferences.getString("schoolCode", "");
        schoolUrl = sharedPreferences.getString("schoolUrl", "");

        DataLoader dloader = new DataLoader(getApplicationContext());
        dloader.setLogin(usr, psw, schoolUrl, schoolCode);

        mainEvalList = findViewById(R.id.main_evals);


        //quickly load offline data if online
        if (isOnline()) {
            loadProgress.post(new Runnable() {
                @Override
                public void run() {
                    loadProgress.setVisibility(ProgressBar.VISIBLE);
                }
            });

            loadProgress.setProgress(0);
            DataLoader dloaderOffline = new DataLoader(getApplicationContext());
            dloaderOffline.setLogin(usr, psw, schoolUrl, schoolCode);

            System.out.println("online");
            try {
                dloaderOffline.getStudentOffline();
            } catch (IOException e) {

                e.printStackTrace();
            }
            progressTo(5);

            evaluations = dloaderOffline.getEvaluations();
            evaluationsevvegi = dloaderOffline.getEvaluationsEvvegi();
            evaluationsfelevi = dloaderOffline.getEvaluationsFelevi();
            mainEvalList.post(new Runnable() {
                public void run() {

                    int showItems = 0;
                    for (int n = 4; n >= 0; n--)
                        if (evaluations.size() >= n) {
                            showItems = n;
                            break;
                        }
                    MainLVAdapter adapter = new MainLVAdapter(new ArrayList<>(evaluations.subList(0, showItems)), getApplicationContext());

                    mainEvalList.setAdapter(adapter);
                    mainEvalList.deferNotifyDataSetChanged();

                    mainEvalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                            if (evaluations.size() > 0)
                                EvaluationsDialog(evaluations.get(position));

                        }
                    });

                }
            });
            progressTo(10);
            Date from = new Date();
            Date to = new Date();
            today = new Date();
            from.setDate(from.getDate() - from.getDay() + 1);
            to.setYear(from.getYear());
            to.setMonth(from.getMonth());
            to.setDate(from.getDate() + 6);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            lessons = new ArrayList<>();
            lessons.clear();
            try {
                lessons = dloaderOffline.getTimetableOffline(simpleDateFormat.format(from), simpleDateFormat.format(to), dloaderOffline.getBearerCode());
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
            ttweek = new Week(from, lessonsm, lessonst, lessonsw, lessonsth, lessonsf);
        }
        progressTo(15);
        user = dloader.getStudentInfo();
        progressTo(20);
        notes = dloader.getNotes();
        progressTo(25);
        absences = dloader.getAbsences();
        progressTo(30);
        subjects = dloader.getSubjects();
        progressTo(35);
        try {
            events = dloader.getEventsOffline();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //get online data (or offline if phone is offline)
        try {
            dloader.doLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressTo(40);
        evaluations = dloader.getEvaluations();
        evaluationsfelevi = dloader.getEvaluationsFelevi();
        evaluationsevvegi = dloader.getEvaluationsEvvegi();
        progressTo(45);

        mainEvalList = findViewById(R.id.main_evals);
        mainEvalList.post(new Runnable() {
            public void run() {
                int showItems = 0;
                for (int n = 4; n >= 0; n--)
                    if (evaluations.size() >= n) {
                        showItems = n;
                        break;
                    }
                MainLVAdapter adapter = new MainLVAdapter(new ArrayList<>(evaluations.subList(0, showItems)), getApplicationContext());

                mainEvalList.setAdapter(adapter);
                mainEvalList.deferNotifyDataSetChanged();

                mainEvalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                        if (evaluations.size() > 0)
                            EvaluationsDialog(evaluations.get(position));

                    }
                });
            }
        });
        Date from = new Date();
        Date to = new Date();
        today = new Date();
        from.setDate(from.getDate() - from.getDay() + 1);
        to.setYear(from.getYear());
        to.setMonth(from.getMonth());
        to.setDate(from.getDate() + 6);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        lessons = new ArrayList<>();
        lessons.clear();
        try {
            lessons = dloader.getTimetable(simpleDateFormat.format(from), simpleDateFormat.format(to), dloader.getBearerCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressTo(50);

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
        ttweek = new Week(from, lessonsm, lessonst, lessonsw, lessonsth, lessonsf);
        user = dloader.getStudentInfo();
        progressTo(55);

        nextLessonInfo.post(new Runnable() {
            public void run() {
                Boolean wasInLessons = false;
                for (Lesson lesson : lessons) {
                    if (lesson.getStart().after(today)) {
                        long millis = lesson.getStart().getTime() - today.getTime();
                        String diff = "";
                        long day = millis / (1000 * 60 * 60 * 24);
                        millis -= (1000 * 60 * 60 * 24) * day;
                        long hour = millis / (1000 * 60 * 60);
                        millis -= (1000 * 60 * 60) * hour;
                        long minute = millis / (1000 * 60);
                        if (day > 0) {
                            diff += day + " nap, ";
                        }
                        if (hour > 0) {
                            diff += hour + " óra, ";
                        }
                        if (minute > 0) {
                            diff += minute + " perc, ";
                        }
                        if (diff.length() > 3) {
                            diff = diff.substring(0, diff.length() - 2);
                            nextLessonInfo.setText(lesson.getSubjectName() + " - " + diff + " múlva (" + lesson.getRoom() + ")");
                        } else {
                            nextLessonInfo.setText(lesson.getSubjectName() + " - " + " most");
                        }

                        wasInLessons = true;
                        break;
                    }
                }
                if (!wasInLessons)
                    ((LinearLayout) nextLessonInfo.getParent()).setVisibility(LinearLayout.GONE);
                else
                    ((LinearLayout) nextLessonInfo.getParent()).setVisibility(LinearLayout.VISIBLE);

            }
        });
        progressTo(60);


        try {
            events = dloader.getEvents(dloader.getBearerCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressTo(70);

        notes = dloader.getNotes();
        progressTo(80);
        absences = dloader.getAbsences();
        progressTo(90);
        subjects = dloader.getSubjects();
        progressTo(100);
        System.out.println("end");
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadProgress.post(new Runnable() {
            public void run() {
                loadProgress.setVisibility(ProgressBar.GONE);
                System.out.println("ended");
            }
        });

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}


