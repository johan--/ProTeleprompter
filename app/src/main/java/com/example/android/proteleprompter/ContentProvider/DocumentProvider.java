package com.example.android.proteleprompter.ContentProvider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DocumentProvider extends ContentProvider {

    private SQLiteDatabase db;
    private DocumentDbHelper mDocumentDbHelper;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();


    private static final int DOCUMENT = 100;
    private static final int DOCUMENT_WITH_ID = 200;

    @Override
    public boolean onCreate() {
        mDocumentDbHelper = new DocumentDbHelper(getContext());
        db = mDocumentDbHelper.getWritableDatabase();

        if (db == null) {
            return false;
        }

        if (db.isReadOnly()) {
            db.close();
            db = null;
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {
            // All Flavors selected
            case DOCUMENT: {
                retCursor = mDocumentDbHelper.getReadableDatabase().query(
                        DocumentContract.DocumentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case DOCUMENT_WITH_ID: {
                retCursor = mDocumentDbHelper.getReadableDatabase().query(
                        DocumentContract.DocumentEntry.TABLE_NAME,
                        projection,
                        DocumentContract.DocumentEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case DOCUMENT: {
                return DocumentContract.DocumentEntry.CONTENT_DIR_TYPE;
            }
            case DOCUMENT_WITH_ID: {
                return DocumentContract.DocumentEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch (URI_MATCHER.match(uri)) {
            case DOCUMENT: {
                long _id = db.insert(DocumentContract.DocumentEntry.TABLE_NAME, null, values);

                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = DocumentContract.DocumentEntry.buildDocumentUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        int numDeleted;
        switch (match) {
            case DOCUMENT:
                numDeleted = db.delete(
                        DocumentContract.DocumentEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        DocumentContract.DocumentEntry.TABLE_NAME + "'");
                break;
            case DOCUMENT_WITH_ID:
                numDeleted = db.delete(DocumentContract.DocumentEntry.TABLE_NAME,
                        DocumentContract.DocumentEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        DocumentContract.DocumentEntry.TABLE_NAME + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numUpdated = 0;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (URI_MATCHER.match(uri)) {
            case DOCUMENT: {
                numUpdated = db.update(DocumentContract.DocumentEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case DOCUMENT_WITH_ID: {
                numUpdated = db.update(DocumentContract.DocumentEntry.TABLE_NAME,
                        values,
                        DocumentContract.DocumentEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DocumentContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, DocumentContract.DocumentEntry.TABLE_NAME, DOCUMENT);
        matcher.addURI(authority, DocumentContract.DocumentEntry.TABLE_NAME + "/#", DOCUMENT_WITH_ID);

        return matcher;
    }
}
