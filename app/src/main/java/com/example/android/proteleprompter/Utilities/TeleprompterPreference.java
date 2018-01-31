package com.example.android.proteleprompter.Utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.proteleprompter.R;

public class TeleprompterPreference {

    public static int getPreferredFontSize(Context context) {

        int fontSize;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForFont = context.getString(R.string.pref_font_size_key);
        String defaultFont = context.getString(R.string.pref_font_size_option_default);

        String value = prefs.getString(keyForFont, defaultFont);

        switch (value) {
            case "20": {
                fontSize = 20;
                break;
            }
            case "30": {
                fontSize = 30;
                break;
            }
            case "40": {
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

        fontColour = prefs.getInt(keyForFontColour, R.color.font_default_colour);

        return fontColour;
    }

    public static int getPreferredBackgroundColour(Context context) {
        int backgroundColour;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForBackgroundColour = context.getString(R.string.pref_display_background_colour_key);

        backgroundColour = prefs.getInt(keyForBackgroundColour, R.color.background_default_colour);


        return backgroundColour;
    }
}