package com.example.android.proteleprompter;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener ,
        ColorDialog.OnColorSelectedListener{

    private final String FONT_COLOUR_PICKER_PREFERENCE_KEY = "fontColour";

    private final String FONT_COLOUR_PICKER_PREFERENCE_TAG = "fontColourTag";

    private final String BACKGROUND_COLOUR_PICKER_PREFERENCE_KEY = "backgroundColour";

    private final String BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG = "backgroundColourTag";



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_scrolling_settings);

        findPreference(FONT_COLOUR_PICKER_PREFERENCE_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showFontColourPickerDialog();

                return true;
            }
        });

        findPreference(BACKGROUND_COLOUR_PICKER_PREFERENCE_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showBackgroundColourPickerDialog();

                return true;
            }
        });

//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//        PreferenceScreen prefScreen = getPreferenceScreen();
//        int count = prefScreen.getPreferenceCount();
//        for (int i = 0; i < count; i++) {
//            Preference p = prefScreen.getPreference(i);
//            if (!(p instanceof CheckBoxPreference) || !(p instanceof Sha)) {
//
//                String value = sharedPreferences.getString(p.getKey(), "fontSize");
//                setPreferenceSummary(p, value);
//
//            }
//        }
    }

    private void showBackgroundColourPickerDialog() {
        new ColorDialog.Builder(getActivity().getBaseContext())
                .setColorShape(ColorShape.CIRCLE) //CIRCLE or SQUARE
                .setTag(BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG) // tags can be useful when multiple components use the picker within an activity
                .show();

    }

    private void showFontColourPickerDialog() {
        new ColorDialog.Builder(this)
                .setColorShape(ColorShape.CIRCLE) //CIRCLE or SQUARE
                .setTag(FONT_COLOUR_PICKER_PREFERENCE_TAG) // tags can be useful when multiple components use the picker within an activity
                .show();
    }


    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Register the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            /* For list preferences, look up the correct display value in */
            /* the preference's 'entries' list (since they have separate labels/values). */
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onColorSelected(int i, String s) {

    }
}
