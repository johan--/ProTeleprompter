package com.example.android.proteleprompter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.Data.DocumentImport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    private static Context mContext;
    private MainFragment mFragmant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        mFragmant = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragment_container, mFragmant)
                .commit();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performFileSearch();


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private static final int READ_REQUEST_CODE = 39;

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("text/rtf");

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
                "text/rtf"};

        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, READ_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri fileImportedUri = data.getData();

            Cursor returnCursor = getContentResolver().query(fileImportedUri, null, null, null, null);

            if (returnCursor != null && returnCursor.moveToFirst()) {
                int fileNameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = returnCursor.getColumnName(fileNameIndex);

                ContentResolver cR = MainActivity.getmContext().getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String fileType = mime.getExtensionFromMimeType(cR.getType(fileImportedUri));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String fileTime = df.format(c.getTime());

                String fileText = readTextFile(fileImportedUri);

                ContentValues fileValue = new ContentValues();

                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME, fileName);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE, fileType);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME, fileTime);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT, fileText);

                getContentResolver().insert(DocumentContract.DocumentEntry.CONTENT_URI, fileValue);

            }
            mFragmant.updateAdapter();

        }

    }

    private String readTextFile(Uri uri){
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
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
