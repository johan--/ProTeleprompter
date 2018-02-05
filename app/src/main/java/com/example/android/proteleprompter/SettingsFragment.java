package com.example.android.proteleprompter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_scrolling_settings);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);

            if (i == count - 1) {
                p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        Intent intent = new Intent(getActivity(), ColourSettingActivity.class);

                        startActivity(intent);

                        return true;
                    }
                });
            }

            if (p instanceof ListPreference) {

                String value = sharedPreferences.getString(p.getKey(), "fontSize");
                setPreferenceSummary(p, value);

            } else if (p instanceof SwitchPreferenceCompat) {
                setSwitchPreferenceSummary(sharedPreferences, p.getKey());
            }
        }
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
//        SharedPreferences colourPrefs = getActivity().getSharedPreferences("coloursPrefs", Context.MODE_PRIVATE);
//        mFontColour = colourPrefs.getInt("fontColour", R.color.font_default_colour);
//        mBackgroundColour = colourPrefs.getInt("backgroundColour", R.color.background_default_colour);

    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

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
        Preference p = findPreference(key);
        if (p instanceof ListPreference) {
            setPreferenceSummary(p, sharedPreferences.getString(key, ""));
        } else if (p instanceof SwitchPreferenceCompat) {
            setSwitchPreferenceSummary(sharedPreferences, key);
        }
    }

    private void setSwitchPreferenceSummary(SharedPreferences sharedPreferences, String key) {
        Preference p = findPreference(key);
        if (sharedPreferences.getBoolean(key, false)) {
            p.setSummary(R.string.pref_mirrorMode_summary_on);
        } else {
            p.setSummary(R.string.pref_mirrorMode_summary_off);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getResources().getString(R.string.pref_display_colour_key))) {

            return true;
        }

        return false;
    }
}
