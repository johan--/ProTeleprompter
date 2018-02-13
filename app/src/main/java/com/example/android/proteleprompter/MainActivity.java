package com.example.android.proteleprompter;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.Utilities.TeleprompterPreference;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_INVITE_CODE = 1226;
    private static final String TAG = MainActivity.class.getName();
    private MainFragment mFragment;
    private int mExtraFromWidget;

    private static final int READ_REQUEST_CODE = 39;

    public static final int IMPORT_FILE_REQUEST_CODE = 8639;

    public static final String IMPORT_FILE_INTENT_NAME = "com.example.android.proteleprompter.importFileFromWidget";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mIntentFromWidgetAddList = getIntent();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        if (mIntentFromWidgetAddList.hasExtra(IMPORT_FILE_INTENT_NAME))
            mExtraFromWidget = mIntentFromWidgetAddList.getIntExtra(IMPORT_FILE_INTENT_NAME, -200);

        if (mExtraFromWidget == IMPORT_FILE_REQUEST_CODE) {
            performFileSearch();
            mExtraFromWidget = 0;
        }

        mFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragment_container, mFragment)
                .commit();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TeleprompterPreference.getFirstRun(MainActivity.this)) {

                    openFileTypeReminder();

                } else {
                    performFileSearch();
                }
            }
        });
//        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData data) {
//                        if (data == null) {
//                            Log.d(TAG, "getInvitation: no data");
//                            return;
//                        }
//
//                        // Get the deep link
//                        Uri deepLink = data.getLink();
//
//                        // Extract invite
//                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
//                        if (invite != null) {
//                            String invitationId = invite.getInvitationId();
//                        }
//
//                        // Handle the deep link
//                        // ...
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "getDynamicLink:onFailure", e);
//                    }
//                });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(IMPORT_FILE_INTENT_NAME)) {
            performFileSearch();
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ProTeleprompterWidget.class));
        //Trigger data update to handle widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.layout.proteleprompter_widget);
        //Now update all widgets
        ProTeleprompterWidget.updatePlantWidgets(this, appWidgetManager, appWidgetIds);
        super.onStop();
    }

    private void openFileTypeReminder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.type_dialog_reminder_title);
        builder.setMessage(R.string.type_dialog_reminder_message);
        builder.setPositiveButton(R.string.type_dialog_reminder_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performFileSearch();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_share) {
            sendInvitation();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE_CODE);
    }


    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword",
                "application/vnd.ms-powerpoint",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/rtf",
                "application/x-rtf",
                "text/richtext",
                "application/vnd.google-apps.document",
                "application/pdf",
                "text/rtf"};

        intent.setType("text/plain");

        startActivityForResult(intent, READ_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri fileImportedUri = data.getData();

            Cursor returnCursor = getContentResolver().query(fileImportedUri, null, null, null, null);

            if (returnCursor != null && returnCursor.moveToFirst()) {
                int fileNameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileNameWithExtension = returnCursor.getString(fileNameIndex);
                String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));

                ContentResolver cR = MainActivity.this.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String fileType = mime.getExtensionFromMimeType(cR.getType(fileImportedUri));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
                String fileTime = df.format(c.getTime());

                String fileText = readTextFromTxtFile(fileImportedUri);

                //TODO: txt is working well, next mission is google document
//                switch (fileType) {
//                    case "txt":
//                        fileText = readTextFromTxtFile(fileImportedUri);
//                        break;
//                    case "rtf":
//                        fileText = readTextFromRtfFile();
//                        break;
//                    case "pdf":
//                        fileText = readTextFromPdfFile(fileImportedUri);
//                        break;
//                    case "doc":
//                        fileText = readTextFromDocFile(fileImportedUri);
//                        break;
//                    case "ppt":
//                        fileText = readTextFromPptFile();
//                        break;
//                    default:
//                        fileText = readTextFromTxtFile(fileImportedUri);
//                }

                ContentValues fileValue = new ContentValues();

                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME, fileName);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE, fileType);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME, fileTime);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT, fileText);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI, fileImportedUri.toString());

                getContentResolver().insert(DocumentContract.DocumentEntry.CONTENT_URI, fileValue);


                returnCursor.close();
                mFragment.updateAdapter();
            }
        }
        if (requestCode == REQUEST_INVITE_CODE && resultCode == RESULT_OK) {
            // Check how many invitations were sent and log.
            String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
            Log.d(TAG, "Invitations sent: " + ids.length);
        } else {
            // Sending failed or it was canceled, show failure message to the user
            Log.d(TAG, "Failed to send invitation.");

        }
    }

    private String readTextFromTxtFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    private String readTextFromRtfFile() {
        String rtfText = "";


        return rtfText;

    }

    private String readTextFromPdfFile(Uri pdfuri) {
        String pdfText = "";

        return pdfText;
    }

    private String readTextFromDocFile(Uri pdfuri) {


        String docText = "";

        return docText;

    }

    private String readTextFromPptFile() {
        String pptText = "";

        return pptText;
    }

    public void fileClick(View view) {
        ImageView imgView = view.findViewById(R.id.iv_fileTypeIcon);
        int fileId = Integer.valueOf(imgView.getTag().toString());
        Intent intent = new Intent(getBaseContext(), ScrollActivity.class);
        intent.putExtra(ScrollActivity.EXTRA_DOCUMENT_ID, fileId);
        startActivity(intent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
