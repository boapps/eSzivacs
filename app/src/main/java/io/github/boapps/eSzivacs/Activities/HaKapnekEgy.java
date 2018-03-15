package io.github.boapps.eSzivacs.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.github.boapps.eSzivacs.Adapters.EditLVAdapter;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.R;

import static io.github.boapps.eSzivacs.Activities.MainPage.evaluations;


public class HaKapnekEgy extends AppCompatActivity {
    ArrayList<Evaluation> subEvals = new ArrayList<>();
    String lesson;
    ListView lessonEvalsLV;
    RadioGroup radioGroup;
    CheckBox checkTZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ha_kapnek_egy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        System.out.println("TANTARGY:");
        System.out.println(intent.getStringExtra("lesson"));
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lesson = null;
            } else {
                lesson = extras.getString("lesson");
            }
        } else {
            lesson = (String) savedInstanceState.getSerializable("lesson");
        }
        System.out.println("\'" + lesson + "\'");
        System.out.println(evaluations.size());
        for (int n = 0; n < evaluations.size(); n++) {
            System.out.println(evaluations.get(n).getSubject());
            System.out.println(evaluations.get(n).getSubjectCategory());
            if (evaluations.get(n).getSubject().equals(lesson))
                subEvals.add(evaluations.get(n));
        }

        Collections.sort(subEvals, new Comparator<Evaluation>() {
            @Override
            public int compare(Evaluation o1, Evaluation o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        lessonEvalsLV = findViewById(R.id.editlv);

        System.out.println(subEvals.size());
        EditLVAdapter mainLVAdapter = new EditLVAdapter(subEvals, this);
        lessonEvalsLV.setAdapter(mainLVAdapter);
        lessonEvalsLV.deferNotifyDataSetChanged();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectEvalDialog();
            }
        });
        updateAvarage();
    }

    public void showSelectEvalDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View evsDialog = li.inflate(R.layout.select_hakap_dialog,
                null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(evsDialog);
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int evalValue = 0;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radioButton:
                        evalValue = 1;
                        break;
                    case R.id.radioButton2:
                        evalValue = 2;
                        break;
                    case R.id.radioButton3:
                        evalValue = 3;
                        break;
                    case R.id.radioButton4:
                        evalValue = 4;
                        break;
                    case R.id.radioButton5:
                        evalValue = 5;
                        break;
                }
                System.out.println("jegy: " + evalValue);
                System.out.println("TZ: " + checkTZ.isChecked());
                SimpleDateFormat format = new SimpleDateFormat("yyyy. MM. dd. ");
                subEvals.add(0, new Evaluation(0, "form", "1-5", "Évközi :P", lesson, lesson, "mode", checkTZ.isChecked() ? "100%" : "200%", String.valueOf(evalValue), String.valueOf(evalValue), "Teacher", format.format(new Date()), format.format(new Date()), "Theme"));
                EditLVAdapter mainLVAdapter = new EditLVAdapter(subEvals, getApplicationContext());
                lessonEvalsLV.setAdapter(mainLVAdapter);
                lessonEvalsLV.deferNotifyDataSetChanged();
                updateAvarage();
            }
        });
        Dialog dialog = alertDialogBuilder.create();
        dialog.show();
        radioGroup = dialog.findViewById(R.id.radioGroup);
        checkTZ = dialog.findViewById(R.id.checkTZ);
    }

    public void updateAvarage() {
        int sum = 0;
        int count = 0;
        for (Evaluation evaluation : subEvals) {
            System.out.println(evaluation.getWeight());
            sum += (Integer.valueOf(evaluation.getNumericValue()) * Integer.valueOf(evaluation.getWeight().substring(0, evaluation.getWeight().indexOf("%"))) / 100);
            count += (Integer.valueOf(evaluation.getWeight().substring(0, evaluation.getWeight().indexOf("%"))) / 100);
        }
        TextView avarageHakapTV = findViewById(R.id.avarage_hakap_tv);
        avarageHakapTV.setText("Átlag: " + String.format("%.2f", (float) sum / count));
    }

}
