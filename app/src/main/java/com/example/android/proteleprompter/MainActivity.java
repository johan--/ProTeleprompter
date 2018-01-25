package com.example.android.proteleprompter;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    private static Context mContext;
    private MainFragment mFragmant;

    private static final int READ_REQUEST_CODE = 39;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        mFragmant = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragment_container, mFragmant)
                .commit();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
                //openFileExplorer();
            }
        });

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

        return super.onOptionsItemSelected(item);
    }


    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("*/*");

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
                "text/rtf"};

        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    private void openFileExplorer(){
        Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        fileintent.setType("*/*");
        try {
            startActivityForResult(fileintent, READ_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri fileImportedUri = data.getData();

            Cursor returnCursor = getContentResolver().query(fileImportedUri, null, null, null, null);

            if (returnCursor != null && returnCursor.moveToFirst()) {
                int fileNameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = returnCursor.getString(fileNameIndex);

                ContentResolver cR = MainActivity.getmContext().getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String fileType = mime.getExtensionFromMimeType(cR.getType(fileImportedUri));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String fileTime = df.format(c.getTime());

                String fileText;

                //TODO: if file type is rtf, doc, pdf and ppt, text need to be detected specifically
                switch (fileType){
                    case "txt": fileText  = readTextFromTxtFile(fileImportedUri); break;
                    case "rtf": fileText  = readTextFromRtfFile(); break;
                    case "ppt": fileText  = readTextFromPdfFile(); break;
                    case "doc": fileText  = readTextFromDocFile(); break;
                    case "pdf": fileText  = readTextFromPptFile(); break;
                    default: fileText  = readTextFromTxtFile(fileImportedUri);
                }

                ContentValues fileValue = new ContentValues();

                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME, fileName);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE, fileType);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME, fileTime);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT, fileText);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI, fileImportedUri.toString());

                getContentResolver().insert(DocumentContract.DocumentEntry.CONTENT_URI, fileValue);

            }
            returnCursor.close();
            mFragmant.updateAdapter();
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

    private String readTextFromRtfFile(){


    }
    private String readTextFromPdfFile(){

    } private String readTextFromDocFile(){

    } private String readTextFromPptFile(){

    }
}
