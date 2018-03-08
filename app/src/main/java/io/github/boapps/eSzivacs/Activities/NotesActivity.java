package io.github.boapps.eSzivacs.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import io.github.boapps.eSzivacs.Adapters.NotesLVAdapter;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.notes;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView notes_lv = findViewById(R.id.notes_lv);
        NotesLVAdapter adapter = new NotesLVAdapter(notes, this);
        notes_lv.setAdapter(adapter);

    }

}
