package com.example.android.proteleprompter;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.Data.Document;
import com.example.android.proteleprompter.Utilities.CameraView;
import com.example.android.proteleprompter.Utilities.CustomImagebutton;
import com.example.android.proteleprompter.Utilities.TeleprompterPreference;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScrollActivityFragment extends Fragment implements  GoogleApiClient.OnConnectionFailedListener,SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static boolean PREFERENCE_HAS_BEEN_UPDATED;
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
    private int mScrollSpeed;
    private long mCurrentPlayTime;
    private int mFileId;

    private static final int REQUEST_INVITE_CODE = 1226;
    private static final int SINGLE_LOADER_ID = 300;

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
    private Handler mScrollingHandler;
    private Runnable mStopWatchRunnable;
    private Runnable mScrollingRunnable;

    private ObjectAnimator mObjectAnimator;

    public ScrollActivityFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//necessary for inflating menu in fragment
        try {
            mFileId = getActivity().getIntent().getIntExtra(ScrollActivity.EXTRA_DOCUMENT_ID, -1);
        } catch (NullPointerException e) {
            Log.e(TAG, e.toString());
        }
//        if (mFileId != -1) {
//            mDocumentContentUri = Uri.parse(mDocument.documentUri);
//            mDocumentContent = mDocument.text;
//        }
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
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

        fmab_ScrollSwitch.setOnMusicFabClickListener(new FloatingMusicActionButton.OnMusicFabClickListener() {
            @Override
            public void onClick(View view) {

//                mTextLines = tv_scrollContentView.getLineCount();
//                mScrollDuration = mTextLines * 1700;

                if (scrolling_running) {
                    stopScrolling();
                    stopStopWatch();
                } else {
                    startScrolling(mScrollSpeed);
                    startStopWatch();
                }
            }
        });


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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SINGLE_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_share){
            sendInvitation();
        }



        return super.onOptionsItemSelected(item);
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE_CODE);
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

        fmab_ScrollSwitch.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_STOP);

        int hours = stopWatch_seconds / 3600;
        int minutes = (stopWatch_seconds % 3600) / 60;
        int secs = stopWatch_seconds % 60;
        String time = String.format(getString(R.string.timer_time), hours, minutes, secs);
        tv_timer.setText(time);

        updateViews();

    }

    //TODO: mirror mode need to be done, add one more option to make users choose horizon or vertical mirror,
    // vertical mirror mode should let users scroll from bottom to top
    private void updateViews() {

        getStoredSettingAttrs();

        tv_scrollContentView.setTextSize(mFontSize);
        tv_scrollContentView.setTextColor(mFontColour);
        tv_scrollContentView.setBackgroundColor(mBackgroundColour);
        if (mMirrorModeOn) {
            tv_scrollContentView.setScaleY(-1);
        } else {
            tv_scrollContentView.setScaleY(1);
        }

    }

    //TODO: 3s count down is not displayed, thinking if it is necessary for this app
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

        stopWatch_running = true;

        mStopWatchRunnable = new Runnable() {
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

        mStopWatchHandler.postDelayed(mStopWatchRunnable, 0);
    }

    private void stopStopWatch() {

        stopWatch_wasRunning = stopWatch_running;

        if (stopWatch_running && mStopWatchHandler!=null) mStopWatchHandler.removeCallbacks(mStopWatchRunnable);

        stopWatch_running = false;
    }

    private void startScrolling(final int speed) {

        scrolling_running = true;

        mScrollingHandler = new Handler();

        mScrollingRunnable = new Runnable() {
            @Override
            public void run() {
                //TODO: speed is not related to font size, which are supposed to be connected with line counts and font size
                sv_scrollView.smoothScrollBy(0, 1);        // 1 is how many pixels you want it to scroll vertically by
                mScrollingHandler.postDelayed(this, speed);     // speed is how many milliseconds you want this thread to run
            }
        };
        mScrollingHandler.postDelayed(mScrollingRunnable, 5000-(stopWatch_seconds*1000)>0 ? 5000-(stopWatch_seconds*1000) : 0);

    }

    private void stopScrolling() {

        if (scrolling_running) mScrollingHandler.removeCallbacks(mScrollingRunnable);

        scrolling_running = false;

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

    private void getStoredSettingAttrs() {

        mFontSize = TeleprompterPreference.getPreferredFontSize(getActivity());
        mMirrorModeOn = TeleprompterPreference.getMirrorIsOn(getActivity());
        mFontColour = TeleprompterPreference.getPreferredFontColour(getActivity());
        mBackgroundColour = TeleprompterPreference.getPreferredBackgroundColour(getActivity());
        mScrollSpeed = TeleprompterPreference.getPreferredScrollingSpeed(getActivity());
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

    @Override
    public void onResume() {
        super.onResume();

        if (stopWatch_wasRunning) {
            stopWatch_running = true;
        }

        if (PREFERENCE_HAS_BEEN_UPDATED) {
            updateViews();
        }
        fmab_ScrollSwitch.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);

    }

    @Override
    public void onStop() {

        if (mCamera != null) mCamera.stopPreview();

        if (scrolling_running) stopScrolling();
        if (stopWatch_running) stopStopWatch();

        fmab_ScrollSwitch.clearAnimation();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mCamera != null) mCamera.release();
        super.onDestroy();

        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCE_HAS_BEEN_UPDATED = true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri SINGLE_DOCUMENT_URI = DocumentContract.DocumentEntry.buildDocumentUri(mFileId);
        return new CursorLoader(getActivity(), SINGLE_DOCUMENT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) return;
        data.moveToFirst();
        String fileContent = data.getString(data.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT));
        tv_scrollContentView.setText(fileContent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_INVITE_CODE && resultCode == RESULT_OK) {
            // Check how many invitations were sent and log.
            String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
            Log.d(TAG, "Invitations sent: " + ids.length);
        } else {
            // Sending failed or it was canceled, show failure message to the user
            Log.d(TAG, "Failed to send invitation.");

        }
    }
}

