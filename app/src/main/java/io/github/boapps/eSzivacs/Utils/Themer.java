package io.github.boapps.eSzivacs.Utils;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;

import io.github.boapps.eSzivacs.R;

/**
 * Created by boa on 19/01/18.
 */

public class Themer {

    public static void selectCurrentTheme(Context context) {
        try {
            switch (Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_frequency", "1"))) {
                case 1:
                    context.setTheme(R.style.AppTheme);
                    break;
                case 2:
                    context.setTheme(R.style.Dark);
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static int getTextColor(Context context) {
        try {
            switch (Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_frequency", "1"))) {
                //            case 1: return Color.BLACK;
                case 2:
                    return Color.WHITE;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return Color.BLACK;
    }


}
