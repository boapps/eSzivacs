package io.github.boapps.eSzivacs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.github.boapps.eSzivacs.Adapters.EditLVAdapter;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.R;

import static io.github.boapps.eSzivacs.Activities.MainPage.evaluations;


public class HaKapnekEgy extends AppCompatActivity {
    ArrayList<Evaluation> subEvals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ha_kapnek_egy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        System.out.println(intent.getStringExtra("lesson"));
        String lesson = intent.getStringExtra("lesson");
        for (int n = 0; n < evaluations.size(); n++) {
            if (evaluations.get(n).getSubject().equals(lesson))
                subEvals.add(evaluations.get(n));
        }
        Collections.sort(subEvals, new Comparator<Evaluation>() {
            @Override
            public int compare(Evaluation o1, Evaluation o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        ListView lessonEvalsLV = findViewById(R.id.editlv);

        System.out.println(subEvals.size());
        EditLVAdapter mainLVAdapter = new EditLVAdapter(subEvals, this);
        lessonEvalsLV.setAdapter(mainLVAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

}
