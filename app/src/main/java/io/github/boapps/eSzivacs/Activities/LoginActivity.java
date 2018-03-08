package io.github.boapps.eSzivacs.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.boapps.eSzivacs.Adapters.SearchAdapter;
import io.github.boapps.eSzivacs.Datas.School;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.AccountManager;
import io.github.boapps.eSzivacs.Utils.DataLoader;
import io.github.boapps.eSzivacs.Utils.Themer;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    SharedPreferences sharedPreferences;
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ListView lv;
    private DataLoader dalr;
    private ArrayList<School> suliArray = new ArrayList<>();
    private ArrayList<School> suliArrayChanged = new ArrayList<>();
    private String schoolCode;
    private String schoolUrl;
    private Button selectSchool;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra("newAccount", false)) {
            Button backLoginBtn = findViewById(R.id.back_login_btn);
            backLoginBtn.setVisibility(View.VISIBLE);
            backLoginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

        mEmailView = findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        selectSchool = findViewById(R.id.select_school);

        selectSchool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog();
            }
        });
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                dalr = new DataLoader(getApplicationContext());
//                try {
//                    dalr.getSchools();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                try {
                    suliArray = dalr.getSchools();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        if (this.getSharedPreferences("prefs", Activity.MODE_PRIVATE).getBoolean("needNotified", true)) {
            final Dialog notDialog = new Dialog(this);
            notDialog.setContentView(R.layout.notif_dialog);
            notDialog.setCancelable(false);
            notDialog.setTitle("Figyelem");
            Button acceptBtn = notDialog.findViewById(R.id.accept_btn);
            acceptBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getApplication().getSharedPreferences("prefs", Activity.MODE_PRIVATE).edit().putBoolean("needNotified", false).apply();
                    notDialog.dismiss();
                }
            });
            notDialog.show();
        }

    }

    public void searchDialog() {
        if (suliArray == null) {
            lv.post(new Runnable() {
                public void run() {

                }
            });

        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.search_dialog);
        Log.d("TAG", "set contentview");
        dialog.setTitle("Iskola");
        dialog.show();
        SearchView searchView = dialog.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("iskola neve");
        lv = dialog.findViewById(R.id.lv);
        Collections.sort(suliArray, new Comparator<School>() {
            @Override
            public int compare(School o1, School o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        suliArrayChanged = suliArray;
        SearchAdapter adapter = new SearchAdapter(suliArrayChanged, this, "");
        lv.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suliArrayChanged = new ArrayList<>();
                for (School sschool : suliArray) {
                    if (sschool.getName().toLowerCase().contains(newText.toLowerCase())) {
                        suliArrayChanged.add(sschool);
                    }
                }
                SearchAdapter adapter = new SearchAdapter(suliArrayChanged, getApplicationContext(), newText);
//                adapter.getFilter().filter(newText);


                lv.setAdapter(adapter);
                lv.deferNotifyDataSetChanged();
                return true;
            }
        });

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                schoolCode = ((School) view.getTag()).getCode();
                schoolUrl = ((School) view.getTag()).getUrl();
                selectSchool.setText(((School) view.getTag()).getName());
                dialog.dismiss();
            }
        });
    }

    private void populateAutoComplete() {
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private DataLoader dloader;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            dloader = new DataLoader(getApplicationContext());
            dloader.setLogin(mEmail, mPassword, schoolUrl, schoolCode);
            try {
                return dloader.isValid();


            } catch (IOException e) {
//                    Log.d("asd", "doInBackground: false");
//                    Log.d("asd", mEmail + " - " + mPassword + " - " + schoolUrl + " - " + schoolCode);
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                dloader = new DataLoader(getApplicationContext());
                dloader.setLogin(mEmail, mPassword, schoolUrl, schoolCode);
                try {
                    dloader.doLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String name = dloader.getStudentInfo().getName();

                sharedPreferences = getApplication().getSharedPreferences(mEmail, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("pw", mPassword);
                editor.putString("schoolCode", schoolCode);
                editor.putString("schoolUrl", schoolUrl);
                editor.apply();

                sharedPreferences = getApplication().getSharedPreferences("prefs", Activity.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("usernames", sharedPreferences.getString("usernames", "") + mEmail + "--");
                editor.putString("names", sharedPreferences.getString("names", "") + name + "--");
                editor.apply();

                AccountManager accountManager = new AccountManager(getApplicationContext());
                accountManager.selectAccount(mEmail);

                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);


                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

class CustomComparator implements Comparator<School> {
    @Override
    public int compare(School o1, School o2) {
        return o1.getName().compareTo(o2.getName());
    }
}

