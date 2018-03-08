package io.github.boapps.eSzivacs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Adapters.AccountsLVAdapter;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.AccountManager;
import io.github.boapps.eSzivacs.Utils.Themer;

public class AccountsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        setContentView(R.layout.activity_accounts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("newAccount", true);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView acc_lv = findViewById(R.id.acc_lv);

        final AccountManager accountManager = new AccountManager(this);
        final ArrayList<String> users = accountManager.getAccounts();
        final ArrayList<String> names = accountManager.getNames();

        AccountsLVAdapter adapter = new AccountsLVAdapter(users, names, this);
        acc_lv.setAdapter(adapter);
        acc_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                accountManager.selectAccount(users.get(position));
                System.out.println("selected: " + users.get(position));
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });
    }
}
