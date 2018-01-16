package com.example.android.proteleprompter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.proteleprompter.Utilities.CustomImagebutton;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScrollActivityFragment extends Fragment {

    private TextView tv_scrollView;
    private FloatingMusicActionButton fmab_ScrollSwitch;
    private TextView tv_timer;
    private CustomImagebutton ib_cameraSwitch;

    private int stopWatch_seconds = 0;
    private boolean stopWatch_running;
    private boolean stopWatch_wasRunning;

    private Handler mHandler;
    private Runnable myRunnable;

    public ScrollActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scroll, container, false);

        if (savedInstanceState != null) {
            stopWatch_seconds = savedInstanceState.getInt("seconds");
            stopWatch_running = savedInstanceState.getBoolean("running", true);
            stopWatch_wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        initViews(root);

        stopWatch_running = true;

        startStopWatch();

        return root;
    }

    private void initViews(View root) {
        tv_scrollView = root.findViewById(R.id.tv_scrollingView);
        fmab_ScrollSwitch = root.findViewById(R.id.fmab_ScrollSwitch);
        tv_timer = root.findViewById(R.id.tv_timer);
        ib_cameraSwitch = root.findViewById(R.id.ib_cameraSwitch);
    }

    private void startStopWatch() {
        myRunnable = new Runnable() {
            @Override
            public void run() {

                int hours = stopWatch_seconds / 3600;
                int minutes = (stopWatch_seconds % 3600) / 60;
                int secs = stopWatch_seconds % 60;
                String time = String.format(getString(R.string.timer_time), hours, minutes, secs);
                tv_timer.setText(time);
                if (stopWatch_running)

                {
                    stopWatch_seconds++;
                }

                mHandler.postDelayed(this, 1000);
            }
        };

        mHandler = new Handler();
        mHandler.post(myRunnable);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("seconds", stopWatch_seconds);
        savedInstanceState.putBoolean("running", stopWatch_running);
        savedInstanceState.putBoolean("wasRunning", stopWatch_wasRunning);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stopWatch_wasRunning) {
            stopWatch_running = true;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWatch_wasRunning = stopWatch_running;
        stopWatch_running = false;
        mHandler.removeCallbacks(myRunnable);
    }
}

