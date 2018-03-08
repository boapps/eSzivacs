package io.github.boapps.eSzivacs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.github.boapps.eSzivacs.Adapters.LessonsLVAdapter;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.subjects;

public class LessonsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        setContentView(R.layout.activity_lessons_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lessons_lv = findViewById(R.id.lessons_lv);

        LessonsLVAdapter adapter = new LessonsLVAdapter(subjects, this);
        lessons_lv.setAdapter(adapter);
        lessons_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), LessonsActivity.class);
                intent.putExtra("subject", subjects.get(position).getSubject());
                startActivity(intent);

            }
        });

    }

}
