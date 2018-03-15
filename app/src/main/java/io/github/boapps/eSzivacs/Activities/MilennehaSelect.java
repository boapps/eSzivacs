package io.github.boapps.eSzivacs.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import io.github.boapps.eSzivacs.Datas.Subject;
import io.github.boapps.eSzivacs.R;

import static io.github.boapps.eSzivacs.Activities.MainPage.subjects;

public class MilennehaSelect extends AppCompatActivity {
    private Dialog selectdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.print("CODERUNONCREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milenneha_select);
        Button hakapna = findViewById(R.id.hakapna);
        hakapna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDialog();
            }
        });
    }

    AlertDialog.Builder alertDialogBuilder;
    public void SelectDialog() {
        System.out.print("CODERUN");
        if (selectdialog == null) {
            LayoutInflater li = LayoutInflater.from(this);
            View selDialog = li.inflate(R.layout.vegi_dialog,
                    null);

            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(selDialog);

        } else if (!selectdialog.isShowing()) {
            LayoutInflater li = LayoutInflater.from(this);
            View evsDialog = li.inflate(R.layout.vegi_dialog,
                    null);

            alertDialogBuilder = new AlertDialog.Builder(
                    this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setView(evsDialog);
        }
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

            selectdialog = alertDialogBuilder.create();
            selectdialog.setTitle("Tantárgy");
            selectdialog.show();

            ListView listselect = selectdialog.findViewById(R.id.listvegi);
            String[] items = new String[subjects.size()];
            for (Subject subject : subjects)
                items[subjects.indexOf(subject)] = subject.getSubject() + "  -  " + subject.getValue();
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
            listselect.setAdapter(itemsAdapter);
            listselect.deferNotifyDataSetChanged();
            listselect.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long l) {
                    System.out.println("listviewwasclicked");
                    System.out.println("clicked: " + position);
                    System.out.println("clicked: " + position);
                    System.out.println("clicked: " + subjects.get(position).getSubject());
                    System.out.println("clicked: " + subjects.get(position).getSubjectName());
                    Intent intent = new Intent(getApplicationContext(), HaKapnekEgy.class);
                    intent.putExtra("lesson", subjects.get(position).getSubject());
                    startActivity(intent);
                }
            });



    }

}
