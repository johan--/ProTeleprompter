package com.example.android.proteleprompter.ContentProvider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DocumentContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.proteleprompter.app";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class DocumentEntry implements BaseColumns {

        public static final String TABLE_NAME = "importedDocumentList";

        public static final String _ID = "_id";

        public static final String COLUMN_DOCUMENT_NAME = "documentName";

        public static final String COLUMN_DOCUMENT_TYPE = "documentType";

        public static final String COLUMN_DOCUMENT_LASTOPENTIME = "documentLastOpenTime";

        public static final String COLUMN_DOCUMENT_CONTENT = "documentContent";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildDocumentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String[] Columns = new String[]{_ID, COLUMN_DOCUMENT_NAME, COLUMN_DOCUMENT_TYPE, COLUMN_DOCUMENT_LASTOPENTIME, COLUMN_DOCUMENT_CONTENT};

    }
}
