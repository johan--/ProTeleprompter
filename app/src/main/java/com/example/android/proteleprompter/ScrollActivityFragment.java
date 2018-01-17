package com.example.android.proteleprompter;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.proteleprompter.Utilities.CameraView;
import com.example.android.proteleprompter.Utilities.CustomImagebutton;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScrollActivityFragment extends Fragment {

    private ScrollView sv_scrollView;
    private FrameLayout fl_cameraFrame;
    private FloatingMusicActionButton fmab_ScrollSwitch;
    private TextView tv_timer;
    private TextView tv_scrollStartCountDown;
    private TextView tv_scrollContentView;
    private ConstraintLayout mLayout;
    private CustomImagebutton ib_cameraSwitch;
    private CameraView mCameraView;
    private Camera mCamera;
    private long mScrollDuration;
    private long mCurrentPlayTime;

    private final String TAG = getActivity().toString();
    private final int REQUEST_CAMERA = 0;

    private int stopWatch_seconds = 0;
    private boolean scrolling_running;
    private boolean stopWatch_running;
    private boolean stopWatch_wasRunning;
    private boolean camera_running;

    private Handler mHandler;
    private Runnable myRunnable;

    private ObjectAnimator mObjectAnimator;

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

        mScrollDuration = 50000;

        mCamera = getCameraInstance();

        startScrollCountDown();

        startStopWatch();

        startScrolling(mScrollDuration);

        fmab_ScrollSwitch.setOnMusicFabClickListener(new FloatingMusicActionButton.OnMusicFabClickListener() {
            @Override
            public void onClick(View view) {
                if (scrolling_running)
                    stopScrolling();
                else startScrolling();
            }
        });

        //TODO: camera permission requesting have problems, maybe its the reason why camera preview screen is black
        ib_cameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camera_running) {
                    Log.i(TAG, "Show camera button pressed. Checking permission.");
                    // BEGIN_INCLUDE(camera_permission)
                    // Check if the Camera permission is already available.
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Camera permission has not been granted.

                        requestCameraPermission();

                    } else {

                        // Camera permissions is already available, show the camera preview.
                        Log.i(TAG,
                                "CAMERA permission has already been granted. Displaying camera preview.");
                        startCamera();
                    }
                } else {
                    closeCamera();
                }
            }

        });
        return root;
    }

    private void initViews(View root) {
        sv_scrollView = root.findViewById(R.id.sv_scrollingView);
        tv_scrollContentView = root.findViewById(R.id.tv_scrollContentView);
        tv_scrollStartCountDown = root.findViewById(R.id.tv_startScrollCountDown);
        fmab_ScrollSwitch = root.findViewById(R.id.fmab_ScrollSwitch);
        tv_timer = root.findViewById(R.id.tv_timer);
        ib_cameraSwitch = root.findViewById(R.id.ib_cameraSwitch);
        mLayout = root.findViewById(R.id.scroll_layout);


        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mCameraView = new CameraView(getActivity(), mCamera);
        fl_cameraFrame = root.findViewById(R.id.cameraScreen);
        fl_cameraFrame.setVisibility(View.INVISIBLE);
    }

    private void startScrollCountDown() {
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_scrollStartCountDown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                tv_scrollStartCountDown.setText("");
            }
        }.start();
    }

    private void startStopWatch() {
        new Handler().postDelayed(new Runnable() {
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
        }, 3000);

        mHandler = new Handler();
        mHandler.post(myRunnable);
    }

    private void startScrolling(final long duration) {

        mObjectAnimator = new ObjectAnimator();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //TODO: duration is not exactly same as stopwatch time,
                // about 5-20s difference depending on duration, needs to be fixed
                scrolling_running = true;
                mObjectAnimator = ObjectAnimator.ofInt(sv_scrollView, "scrollY", tv_scrollContentView.getBottom()).setDuration(duration);
                mObjectAnimator.start();
                fmab_ScrollSwitch.playAnimation();
            }
        }, 3000);

    }

    private void stopScrolling() {
        mCurrentPlayTime = mObjectAnimator.getCurrentPlayTime();
        scrolling_running = false;
        mObjectAnimator.cancel();
    }

    private void startScrolling() {
        scrolling_running = true;
        mObjectAnimator.setCurrentPlayTime(mCurrentPlayTime);
        mObjectAnimator.start();
    }

    private void startCamera() {
        fl_cameraFrame.setVisibility(View.VISIBLE);
        fl_cameraFrame.addView(mCameraView);
    }

    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    private void closeCamera() {
        fl_cameraFrame.removeView(mCameraView);
        fl_cameraFrame.setVisibility(View.INVISIBLE);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("seconds", stopWatch_seconds);
        savedInstanceState.putBoolean("running", stopWatch_running);
        savedInstanceState.putBoolean("wasRunning", stopWatch_wasRunning);
    }

    //TODO: app will crash when fragment is destoryed
    @Override
    public void onResume() {
        super.onResume();
        if (stopWatch_wasRunning) {
            stopWatch_running = true;
        }
    }

    //TODO: app will crash when fragment is destoryed
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWatch_wasRunning = stopWatch_running;
        stopWatch_running = false;
        mHandler.removeCallbacks(myRunnable);
    }
}

