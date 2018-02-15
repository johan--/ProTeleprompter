package com.example.android.proteleprompter;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

public class ColourSettingActivity extends AppCompatActivity implements ColorDialog.OnColorSelectedListener, ColourSettingsFragment.OnColourSettingFragmentListener {


    private final String FONT_COLOUR_PICKER_PREFERENCE_TAG = "fontColourTag";

    private final String BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG = "backgroundColourTag";

    private int mFontColour;

    private int mBackgroundColour;

    private  ColourSettingsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_colour_settings);
        mFragment = new ColourSettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.scroll_colour_settings_fragment_container, mFragment)
                .commit();

        Toolbar toolbar = findViewById(R.id.colourSettingActivity_toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onColorSelected(int i, String s) {

        switch (s){
            case FONT_COLOUR_PICKER_PREFERENCE_TAG:
                //change the font color with newColor
                mFragment.setColourPrefColor(s, i);
                mFontColour = i;
                break;
            case BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG:
                //change the background color with newColor
                mBackgroundColour = i;
                mFragment.setColourPrefColor(s, i);
                break;
        }
    }

    public void showFontColourPickerDialog(String tag) {
        if (tag.equals(FONT_COLOUR_PICKER_PREFERENCE_TAG))
            new ColorDialog.Builder(this)
                    .setColorShape(ColorShape.CIRCLE) //CIRCLE or SQUARE
                    .setTag(FONT_COLOUR_PICKER_PREFERENCE_TAG) // tags can be useful when multiple components use the picker within an activity
                    .show();
        if (tag.equals(BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG))
            new ColorDialog.Builder(this)
                    .setColorShape(ColorShape.CIRCLE) //CIRCLE or SQUARE
                    .setTag(BACKGROUND_COLOUR_PICKER_PREFERENCE_TAG) // tags can be useful when multiple components use the picker within an activity
                    .show();
    }

    @Override
    public void onFragmentInteraction(String tag) {

        showFontColourPickerDialog(tag);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = sharedPref.edit();

        prefEditor.putInt("fontColour", mFontColour);
        prefEditor.putInt("backgroundColour", mBackgroundColour);
        prefEditor.apply();

    }

}
