package io.github.boapps.eSzivacs.Utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import io.github.boapps.eSzivacs.Activities.EvaluationListActivity;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.R;

public class BackgroundTaskService extends GcmTaskService {

    public static final String GCM_ONEOFF_TAG = "oneoff|[0,0]";
    public static final String GCM_REPEAT_TAG = "repeat|[7200,1800]";
    private static final String TAG = BackgroundTaskService.class.getSimpleName();

    public static void scheduleOneOff(Context context) {
        //in this method, single OneOff task is scheduled (the target service that will be called is BackgroundTaskService.class)
        Bundle data = new Bundle();
        data.putString("some key", "some budle data");
        try {
            OneoffTask oneoff = new OneoffTask.Builder()
                    //specify target service - must extend GcmTaskService
                    .setService(BackgroundTaskService.class)
                    //tag that is unique to this task (can be used to cancel task)
                    .setTag(GCM_ONEOFF_TAG)
                    //executed between 0 - 10s from now
                    .setExecutionWindow(10, 10)
                    //set required network state, this line is optional
                    .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                    //request that charging must be connected, this line is optional
                    .setRequiresCharging(false)
                    //set some data we want to pass to our task
                    .setExtras(data)
                    //if another task with same tag is already scheduled, replace it with this task
                    .setUpdateCurrent(true)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(oneoff);
            Log.v(TAG, "oneoff task scheduled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scheduleRepeat(Context context) {
        //in this method, single Repeating task is scheduled (the target service that will be called is BackgroundTaskService.class)
        try {
            PeriodicTask periodic = new PeriodicTask.Builder()
                    //specify target service - must extend GcmTaskService
                    .setService(BackgroundTaskService.class)
                    //repeat every 60 seconds
                    .setPeriod(10 * 60) //10 mins
//                .setPeriod(60) //1 hour
//		        specify how much earlier the task can be executed (in seconds)
                    .setFlex(5 * 60) //5 mins
                    //tag that is unique to this task (can be used to cancel task)
                    .setTag(GCM_REPEAT_TAG)
                    //whether the task persists after device reboot
                    .setPersisted(true)
                    //if another task with same tag is already scheduled, replace it with this task
                    .setUpdateCurrent(true)
                    //set required network state, this line is optional
                    .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                    //request that charging must be connected, this line is optional
                    .setRequiresCharging(false)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(periodic);
            Log.v(TAG, "repeating task scheduled");
        } catch (Exception e) {
            Log.e(TAG, "scheduling failed");
            e.printStackTrace();
        }
    }

    public static void cancelOneOff(Context context) {
        GcmNetworkManager
                .getInstance(context)
                .cancelTask(GCM_ONEOFF_TAG, BackgroundTaskService.class);
    }

    public static void cancelRepeat(Context context) {
        GcmNetworkManager
                .getInstance(context)
                .cancelTask(GCM_REPEAT_TAG, BackgroundTaskService.class);
    }

    public static void cancelAll(Context context) {
        GcmNetworkManager
                .getInstance(context)
                .cancelAllTasks(BackgroundTaskService.class);
    }

    @Override
    public void onInitializeTasks() {
        //called when app is updated to a new version, reinstalled etc.
        //you have to schedule your repeating tasks again
        super.onInitializeTasks();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Bundle extras = taskParams.getExtras();

        Handler h = new Handler(getMainLooper());
        Log.v(TAG, "onRunTask");
        if (taskParams.getTag().equals(GCM_ONEOFF_TAG)) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BackgroundTaskService.this, "ONEOFF executed", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (taskParams.getTag().equals(GCM_REPEAT_TAG)) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    if (isOnline()) {
                        AccountManager accountManager = new AccountManager(getApplicationContext());
                        String usr = accountManager.getSelectedAccount();
                        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(usr, Activity.MODE_PRIVATE);
                        String psw = sharedPreferences.getString("pw", "");
                        String schoolCode = sharedPreferences.getString("schoolCode", "");
                        String schoolUrl = sharedPreferences.getString("schoolUrl", "");

                        DataLoader dloader = new DataLoader(getApplicationContext());
                        dloader.setLogin(usr, psw, schoolUrl, schoolCode);
                        try {
                            dloader.doLogin();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Evaluation> evaluations = dloader.getNewEvaluations();

                        for (Evaluation evaluation : evaluations) {
                            System.out.println(evaluation.getSubject() + evaluation.getNumericValue());
                            String CHANNEL_ID = "jegyek";
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_school_black_24px)
                                            .setContentTitle(evaluation.getSubject() + " " + evaluation.getNumericValue())
                                            .setColor(Color.parseColor("#bf360c"))
                                            .setContentText(evaluation.getTheme());
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                            Intent intent = new Intent(getApplicationContext(), EvaluationListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            PendingIntent pendingIntentintent = PendingIntent.getActivity(getApplicationContext(), 0,
                                    intent, 0);
                            mBuilder.setContentIntent(pendingIntentintent);

                            mNotificationManager.notify(m, mBuilder.build());

                        }
                    }


                }
            });
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
