package io.github.boapps.eSzivacs.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

public class AboutActivity extends AppCompatActivity {
    //This is where I tell people about contact info, creation of e-Szivacs and other stuff regarding the application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //send feedback
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "eszivacs@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
