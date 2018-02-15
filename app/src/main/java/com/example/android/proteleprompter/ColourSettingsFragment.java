package com.example.android.proteleprompter;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;


import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;


public class ColourSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        ColorDialog.OnColorSelectedListener {

    private final String FONT_COLOUR_PICKER_PREFERENCE_TAG = "fontColourTag";

    private final String BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG = "backgroundColourTag";

    private ColorPreferenceCompat fontColourPreference;

    private ColorPreferenceCompat backgroundColourPreference;

    private OnColourSettingFragmentListener mListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_scrolling_settings_display);

        fontColourPreference = (ColorPreferenceCompat) getPreferenceScreen().getPreference(0);

        backgroundColourPreference = (ColorPreferenceCompat) getPreferenceScreen().getPreference(1);

        fontColourPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                mListener.onFragmentInteraction(FONT_COLOUR_PICKER_PREFERENCE_TAG);

                return true;
            }
        });


        backgroundColourPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                mListener.onFragmentInteraction(BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG);

                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnColourSettingFragmentListener) {
            mListener = (OnColourSettingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnColourSettingFragmentListener");
        }
    }

    public void setColourPrefColor(String tag, int colour){
        if(tag.equals(FONT_COLOUR_PICKER_PREFERENCE_TAG)){
            fontColourPreference.setValue(colour);
        }

        if(tag.equals(BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG)){
            backgroundColourPreference.setValue(colour);
        }
    }


    @Override
    public void onColorSelected(int i, String s) {

    }

    public interface OnColourSettingFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String tag);
    }
}
