package com.example.android.proteleprompter.Data;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DocumentImport extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 39;

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {"application/pdf",
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

        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

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

                MainActivity.getmContext().getContentResolver().insert(DocumentContract.DocumentEntry.CONTENT_URI, fileValue);

            }

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

