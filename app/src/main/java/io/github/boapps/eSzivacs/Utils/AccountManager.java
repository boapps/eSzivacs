package io.github.boapps.eSzivacs.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by boa on 30/10/17.
 */

public class AccountManager {
    Context context;
    SharedPreferences sharedPreferences;

    public AccountManager(Context context) {
        this.context = context;
    }

    public ArrayList<String> getAccounts() {
        sharedPreferences = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        String usernames = sharedPreferences.getString("usernames", "");
        System.out.println(usernames);
        ArrayList<String> users = new ArrayList<>();
        while (usernames.contains("--")) {
            System.out.println(usernames.substring(0, usernames.indexOf("--")));
            users.add(usernames.substring(0, usernames.indexOf("--")));
            usernames = usernames.replace(usernames.substring(0, usernames.indexOf("--") + 2), "");
        }
        return users;
    }

    public ArrayList<String> getNames() {
        sharedPreferences = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        String names = sharedPreferences.getString("names", "");
        ArrayList<String> users = new ArrayList<>();
        while (names.contains("--")) {
            System.out.println(names.substring(0, names.indexOf("--")));
            users.add(names.substring(0, names.indexOf("--")));
            names = names.replace(names.substring(0, names.indexOf("--") + 2), "");
        }
        return users;
    }

    public void removeAccount(String username) {
        sharedPreferences = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        String usernames = sharedPreferences.getString("usernames", "");
        System.out.println(usernames);
        usernames = usernames.replace(username + "--", "");
        sharedPreferences.edit().putString("usernames", usernames).apply();
        sharedPreferences = context.getSharedPreferences(username, Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        if (username.equals(getSelectedAccount())) {
            if (getAccounts().size() > 0)
                this.selectAccount(getAccounts().get(0));
            else
                this.selectAccount("");
        }

    }

    public void selectAccount(String username) {
        sharedPreferences = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString("selected", username).apply();
    }

    public String getSelectedAccount() {
        sharedPreferences = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("selected", "");
    }


//    public void addAccount(String username) {
//
//    }
}
