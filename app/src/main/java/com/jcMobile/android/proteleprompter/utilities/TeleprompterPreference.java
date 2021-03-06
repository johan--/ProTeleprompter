package com.jcMobile.android.proteleprompter.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.jcMobile.android.proteleprompter.R;

public class TeleprompterPreference {

    public static int getPreferredFontSize(Context context) {

        int fontSize;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForFont = context.getString(R.string.pref_font_size_key);
        String defaultFont = context.getString(R.string.pref_font_size_option_default);

        String value = prefs.getString(keyForFont, defaultFont);

        switch (value) {
            case "Small": {
                fontSize = 20;
                break;
            }
            case "Normal": {
                fontSize = 30;
                break;
            }
            case "Large": {
                fontSize = 40;
                break;
            }
            default:
                fontSize = 30;
        }
        return fontSize;
    }

    public static boolean getMirrorIsOn(Context context) {
        boolean mirrorModeOn;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForMirrorMode = context.getString(R.string.pref_mirrorMode_key);
        mirrorModeOn = prefs.getBoolean(keyForMirrorMode, false);
        return mirrorModeOn;
    }

    public static int getPreferredFontColour(Context context) {
        int fontColour;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForFontColour = context.getString(R.string.pref_display_font_colour_key);

        fontColour = prefs.getInt(keyForFontColour, -16777216);//-16777216 is integer value of black colour.

        return fontColour;
    }

    public static int getPreferredBackgroundColour(Context context) {
        int backgroundColour;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForBackgroundColour = context.getString(R.string.pref_display_background_colour_key);

        backgroundColour = prefs.getInt(keyForBackgroundColour, -1);//-1 is integer value of white colour

        return backgroundColour;
    }

    public static int getPreferredScrollingSpeed(Context context) {
        int scrollingSpeed;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForScrollingSpeed = context.getString(R.string.pref_scrolling_speed_key);

        String defaultFont = context.getString(R.string.pref_scrolling_speed_default);
        String value = prefs.getString(keyForScrollingSpeed, defaultFont);

        switch (value) {
            case "Lazy": {
                scrollingSpeed = 40;
                break;
            }
            case "Slow": {
                scrollingSpeed = 30;
                break;
            }
            case "Normal": {
                scrollingSpeed = 25;
                break;
            }
            case "Fast": {
                scrollingSpeed = 20;
                break;
            }
            case "High-speed": {
                scrollingSpeed = 15;
                break;
            }
            default:
                scrollingSpeed = 25;
        }
        return scrollingSpeed;
    }

    public static boolean getFirstRun(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (prefs.getBoolean("firstRun", true)) {
            editor.putBoolean("firstRun", false).apply();
            return true;
        } else {
            return false;
        }
    }

    public static int getRecentFileId(Context context){
        int recentFileId;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForRecentFile = context.getString(R.string.pref_recent_files_Id);

        recentFileId = prefs.getInt(keyForRecentFile, -1);

        return recentFileId;
    }
}
