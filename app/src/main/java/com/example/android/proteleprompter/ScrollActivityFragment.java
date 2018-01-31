package com.example.android.proteleprompter;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.proteleprompter.Data.Document;
import com.example.android.proteleprompter.Utilities.CameraView;
import com.example.android.proteleprompter.Utilities.CustomImagebutton;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScrollActivityFragment extends Fragment{

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
    private Document mDocument;
    private Uri mDocumentContentUri;
    private String mDocumentContent;
    private long mScrollDuration;
    private long mCurrentPlayTime;

    private final String TAG = "ScrollFragment";
    private final int REQUEST_CAMERA = 330;
    private static final int FRONT_CAMERA_CODE = 1;

    private int stopWatch_seconds = 0;
    private boolean scrolling_running;
    private boolean stopWatch_running;
    private boolean stopWatch_wasRunning;
    private boolean camera_running;

    private int mFontSize;
    private boolean mMirrorModeOn;
    private int mFontColour;
    private int mBackgroundColour;

    private Handler mStopWatchHandler;
    private Runnable mStopWatchRunnable;

    private ObjectAnimator mObjectAnimator;

    public ScrollActivityFragment() {
    }


    //TODO: retrieve document data from intent
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//necessary for inflating menu in fragment
        try {
            mDocument = (Document) getActivity().getIntent().getBundleExtra("bundle").getSerializable("document");
        } catch (NullPointerException e) {
            Log.e(TAG, e.toString());
        }
        if (mDocument != null) {
            mDocumentContentUri = Uri.parse(mDocument.documentUri);
            mDocumentContent = mDocument.text;
        }
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

        startScrollCountDown();

        startStopWatch();

        startScrolling(mScrollDuration);

        fmab_ScrollSwitch.setOnMusicFabClickListener(new FloatingMusicActionButton.OnMusicFabClickListener() {
            @Override
            public void onClick(View view) {
                if (scrolling_running)
                    stopScrolling();
                else restartScrolling();
            }
        });

        //TODO: camera permission have issues, after clicking allow, camera won't open until press button again
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

    @Override
    public void onStart() {
        super.onStart();
        updateViews();
    }

    private void updateViews(){
        getStoredSettingAttrs();


        tv_scrollContentView.setTextSize(mFontSize);

//        tv_scrollContentView.setTextColor(mFontColour);
//        tv_scrollContentView.setBackgroundColor(mBackgroundColour);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_scroll, menu);
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    private void initViews(View root) {

        sv_scrollView = root.findViewById(R.id.sv_scrollingView);
        tv_scrollContentView = root.findViewById(R.id.tv_scrollContentView);
        tv_scrollStartCountDown = root.findViewById(R.id.tv_startScrollCountDown);
        fmab_ScrollSwitch = root.findViewById(R.id.fmab_ScrollSwitch);
        tv_timer = root.findViewById(R.id.tv_timer);
        ib_cameraSwitch = root.findViewById(R.id.ib_cameraSwitch);
        mLayout = root.findViewById(R.id.scroll_layout);

        fl_cameraFrame = root.findViewById(R.id.cameraScreen);
        fl_cameraFrame.setVisibility(View.INVISIBLE);

        tv_scrollContentView.setText(mDocumentContent);
        fmab_ScrollSwitch.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_STOP);

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

        mStopWatchHandler = new Handler();

        mStopWatchRunnable = new Runnable(){
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

                mStopWatchHandler.postDelayed(mStopWatchRunnable, 1000);
            }
        };


        mStopWatchHandler.postDelayed(mStopWatchRunnable, 3000);
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
                fmab_ScrollSwitch.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);

                //mScrollingHandler.postDelayed(this, 100);


            }
        }, 3000);

//        mScrollingHandler = new Handler();
//        mScrollingHandler.post(mScrollingRunnable);
    }

    private void stopScrolling() {
        mCurrentPlayTime = mObjectAnimator.getCurrentPlayTime();
        scrolling_running = false;
        mObjectAnimator.cancel();
    }

    private void restartScrolling() {
        scrolling_running = true;
        mObjectAnimator.setCurrentPlayTime(mCurrentPlayTime);
        mObjectAnimator.start();
    }

    //TODO: opening and closing camera would make UI irresponsive for about 1s
    private void startCamera() {
        camera_running = true;
        if (mCamera == null)
            mCamera = getCameraInstance();
        mCameraView = new CameraView(getActivity(), mCamera);
        fl_cameraFrame.addView(mCameraView);
        fl_cameraFrame.setVisibility(View.VISIBLE);

    }

    private void closeCamera() {
        fl_cameraFrame.removeView(mCameraView);
        mCamera.stopPreview();
        camera_running = false;
        fl_cameraFrame.setVisibility(View.INVISIBLE);
    }

    private void getStoredSettingAttrs(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mFontSize = sharedPreferences.getInt("fontSize", 30);
        mMirrorModeOn = sharedPreferences.getBoolean("mirrorMode", false);
        mFontColour = sharedPreferences.getInt("fontColour", R.color.font_default_colour);
        mBackgroundColour = sharedPreferences.getInt("backgroundColour", R.color.background_default_colour);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(FRONT_CAMERA_CODE); //1 means front camera, 0 means back camera
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
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
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startCamera();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("seconds", stopWatch_seconds);
        savedInstanceState.putBoolean("running", stopWatch_running);
        savedInstanceState.putBoolean("wasRunning", stopWatch_wasRunning);
    }

    //TODO: app will crash when fragment is destroyed because of run background thread
    @Override
    public void onResume() {
        super.onResume();

        if (stopWatch_wasRunning) {
            stopWatch_running = true;
        }
    }

    @Override
    public void onStop() {

        if (mCamera != null) mCamera.stopPreview();
        stopWatch_wasRunning = stopWatch_running;
        stopWatch_running = false;
        if(mCamera != null) mCamera.release();
        mStopWatchHandler.removeCallbacks(mStopWatchRunnable);

        fmab_ScrollSwitch.clearAnimation();

        super.onStop();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}

