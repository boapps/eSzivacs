package io.github.boapps.eSzivacs.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.boapps.eSzivacs.Activities.MainPage;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.AccountManager;
import io.github.boapps.eSzivacs.Utils.Themer;

/**
 * Created by boa on 24/09/17.
 */

public class AccountsLVAdapter extends BaseAdapter {
    LayoutInflater inflator = null;
    Context context;
    private ArrayList<String> accounts;
    private ArrayList<String> names;

    public AccountsLVAdapter(ArrayList<String> accounts, ArrayList<String> names, Context con) {
        this.accounts = accounts;
        this.names = names;
        context = con;
        Themer.selectCurrentTheme(context);
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int i) {
        return accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            inflator = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.account_lv_item, null);
        }
        TextView accountNameTv = view.findViewById(R.id.account_name_tv);
        accountNameTv.setText(accounts.get(i) + " (" + names.get(i) + ")");
        accountNameTv.setTextColor(Themer.getTextColor(context));
        ImageButton deleteBtn = view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete account")
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AccountManager accountManager = new AccountManager(context);
                                accountManager.removeAccount(accounts.get(i));
                                accounts.remove(i);
                                notifyDataSetChanged();
                                Intent intent = new Intent(context, MainPage.class);
                                context.startActivity(intent);

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        return view;
    }
}
