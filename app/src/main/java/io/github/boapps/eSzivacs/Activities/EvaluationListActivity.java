package io.github.boapps.eSzivacs.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.boapps.eSzivacs.Adapters.MainLVAdapter;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.Datas.Student;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.AccountManager;
import io.github.boapps.eSzivacs.Utils.DataLoader;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.evaluations;
import static io.github.boapps.eSzivacs.Activities.MainPage.evaluationsevvegi;
import static io.github.boapps.eSzivacs.Activities.MainPage.evaluationsfelevi;

public class EvaluationListActivity extends AppCompatActivity {

    public ArrayList<Lesson> lessons;
    public Student user;
    private Dialog dialog;
    private Dialog vegidialog;
    private SharedPreferences sharedPreferences;
    private String usr;
    private String psw;
    private String schoolCode;
    private String schoolUrl;
    private ListView mainListv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        setContentView(R.layout.activity_evaluation_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainListv = findViewById(R.id.evaluation_lv);

        if (evaluations.size() > 0) {
            MainLVAdapter adapter = new MainLVAdapter(evaluations, getApplication());
            mainListv.setAdapter(adapter);
            mainListv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                    EvaluationsDialog(evaluations.get(position));
                }
            });
            mainListv.deferNotifyDataSetChanged();
        }

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Thread(new Runnable() {
                            public void run() {
                                AccountManager accountManager = new AccountManager(getApplicationContext());
                                usr = accountManager.getSelectedAccount();
                                sharedPreferences = getApplication().getSharedPreferences(usr, Activity.MODE_PRIVATE);
                                psw = sharedPreferences.getString("pw", "");
                                schoolCode = sharedPreferences.getString("schoolCode", "");
                                schoolUrl = sharedPreferences.getString("schoolUrl", "");

                                DataLoader dloader = new DataLoader(getApplicationContext());
                                dloader.setLogin(usr, psw, schoolUrl, schoolCode);
                                try {
                                    dloader.doLogin();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                evaluations = dloader.getEvaluations();

                                mainListv.post(new Runnable() {
                                    public void run() {
                                        MainLVAdapter adapter = new MainLVAdapter(evaluations, getApplication());
                                        mainListv.setAdapter(adapter);
                                        mainListv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                                                EvaluationsDialog(evaluations.get(position));
                                            }
                                        });
                                        mainListv.deferNotifyDataSetChanged();
                                    }
                                });

                                refreshLayout.post(new Runnable() {
                                    public void run() {
                                        refreshLayout.setRefreshing(false);
                                    }
                                });

                                user = dloader.getStudentInfo();

                            }
                        }).start();
                    }
                }
        );

        new Thread(new Runnable() {
            public void run() {
                sharedPreferences = getApplication().getSharedPreferences("prefs", Activity.MODE_PRIVATE);
                AccountManager accountManager = new AccountManager(getApplicationContext());
                usr = accountManager.getSelectedAccount();
                sharedPreferences = getApplication().getSharedPreferences(usr, Activity.MODE_PRIVATE);
                psw = sharedPreferences.getString("pw", "");
                schoolCode = sharedPreferences.getString("schoolCode", "");
                schoolUrl = sharedPreferences.getString("schoolUrl", "");

                DataLoader dloader = new DataLoader(getApplicationContext());
                dloader.setLogin(usr, psw, schoolUrl, schoolCode);
                try {
                    dloader.doLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                evaluations = dloader.getEvaluations();

                mainListv = findViewById(R.id.evaluation_lv);
                if (evaluations.size() > 0) {
                    mainListv.post(new Runnable() {
                        public void run() {
                            MainLVAdapter adapter = new MainLVAdapter(evaluations, getApplication());
                            mainListv.setAdapter(adapter);
                            mainListv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                                    EvaluationsDialog(evaluations.get(position));
                                }
                            });
                            mainListv.deferNotifyDataSetChanged();
                        }
                    });
                }
            }
        });

        final Button felevi = findViewById(R.id.felevi);
        felevi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VegiDialog(0);
            }
        });
        Button evvegi = findViewById(R.id.evvegi);

        if (evaluationsfelevi.size() > 0)
            felevi.setVisibility(Button.VISIBLE);
        else
            felevi.setVisibility(Button.INVISIBLE);

        if (evaluationsevvegi.size() > 0)
            evvegi.setVisibility(Button.VISIBLE);
        else
            evvegi.setVisibility(Button.INVISIBLE);


    }

    public void VegiDialog(int vegiID) {
        if (vegidialog == null) {
            LayoutInflater li = LayoutInflater.from(this);
            View vegDialog = li.inflate(R.layout.vegi_dialog,
                    null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(vegDialog);
//                    alertDialogBuilder.setNegativeButton(getString(R.string.title_menu_share), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent sendIntent = new Intent();
//                            sendIntent.setAction(Intent.ACTION_SEND);
//                            sendIntent.putExtra(Intent.EXTRA_TEXT, evaluation.getSubject() + " - " + evaluation.getNumericValue());
//                            sendIntent.setType("text/plain");
//                            startActivity(sendIntent);
//                        }
//                    });

            vegidialog = alertDialogBuilder.create();

            if (vegiID == 0)
                vegidialog.setTitle("Félévi");
            else if (vegiID == 1)
                vegidialog.setTitle("Évvégi");

            vegidialog.show();

            ListView listvegi = vegidialog.findViewById(R.id.listvegi);
            String[] items = new String[evaluationsfelevi.size()];
            System.out.println(evaluationsfelevi.size());
            for (Evaluation evaluation : evaluationsfelevi)
                items[evaluationsfelevi.indexOf(evaluation)] = evaluation.getSubject() + "  -  " + evaluation.getNumericValue();
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
            listvegi.setAdapter(itemsAdapter);
            listvegi.deferNotifyDataSetChanged();

        } else if (!vegidialog.isShowing()) {
            LayoutInflater li = LayoutInflater.from(this);
            View evsDialog = li.inflate(R.layout.vegi_dialog,
                    null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setView(evsDialog);
//                    alertDialogBuilder.setNegativeButton(getString(R.string.title_menu_share), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent sendIntent = new Intent();
//                            sendIntent.setAction(Intent.ACTION_SEND);
//                            sendIntent.putExtra(Intent.EXTRA_TEXT, evaluation.getSubject() + " - " + evaluation.getNumericValue());
//                            sendIntent.setType("text/plain");
//                            startActivity(sendIntent);
//                        }
//                    });

            vegidialog = alertDialogBuilder.create();
            if (vegiID == 0)
                vegidialog.setTitle("Félévi");
            else if (vegiID == 1)
                vegidialog.setTitle("Évvégi");
            vegidialog.show();

            ListView listvegi = vegidialog.findViewById(R.id.listvegi);
            String[] items = new String[evaluationsfelevi.size()];
            for (Evaluation evaluation : evaluationsfelevi)
                items[evaluationsfelevi.indexOf(evaluation)] = evaluation.getSubject() + "  -  " + evaluation.getNumericValue();
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
            listvegi.setAdapter(itemsAdapter);

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
            alertDialogBuilder.setNegativeButton(getString(R.string.title_menu_share), new DialogInterface.OnClickListener() {
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
//            dialog.setTitle(getString(R.string.evaluation_title));
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
                dateTX.setText(evaluation.getDate());
            }
//            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        } else if (!dialog.isShowing()) {
            LayoutInflater li = LayoutInflater.from(this);
            View evsDialog = li.inflate(R.layout.evs_dialog,
                    null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setView(evsDialog);
            alertDialogBuilder.setNegativeButton(getString(R.string.title_menu_share), new DialogInterface.OnClickListener() {
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
//            dialog.setTitle(getString(R.string.evaluation_title));
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
                dateTX.setText(evaluation.getDate());
            }
        }
    }
}
