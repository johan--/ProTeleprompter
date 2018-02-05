package com.example.android.proteleprompter.Adaptor;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.Data.Document;
import com.example.android.proteleprompter.R;
import com.example.android.proteleprompter.ScrollActivity;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdaptor extends CursorRecyclerViewAdapter {

    public List<Document> mDocumentList = new ArrayList<>();

    private Document mDocument;

    private Context mContext;

    private OnEditOrDeleteFilesListener mListener;

    public DocumentAdaptor(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_document, parent, false);
        final documentsListViewHolder vh = new documentsListViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openScrollingActivity(vh);

            }
        });
        return vh;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        documentsListViewHolder holder = (documentsListViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setDataForViews(cursor);
        mDocumentList.add(mDocument);
    }

    public void setOnEditFilesListener(OnEditOrDeleteFilesListener listener) {
        mListener = listener;
    }

    private void openScrollingActivity(documentsListViewHolder vh) {

        Document document = mDocumentList.get(vh.getLayoutPosition());

        Class mScrollActivity = ScrollActivity.class;

        Intent startScrollActivityIntent = new Intent(mContext, mScrollActivity);

        Bundle documentBundle = new Bundle();

        documentBundle.putSerializable("document", document);

        startScrollActivityIntent.putExtra("bundle", documentBundle);

        ActivityCompat.startActivity(mContext, startScrollActivityIntent, null);
    }

    public class documentsListViewHolder extends RecyclerView.ViewHolder {

        TextView fileTitle_tv;
        TextView fileOpenTime_tv;
        ImageView fileTypeImage_iv;
        ImageButton editButton_ib;
        ImageButton deleteButton_ib;

        public documentsListViewHolder(View view) {
            super(view);
            fileTitle_tv = view.findViewById(R.id.tv_fileTitle);
            fileOpenTime_tv = view.findViewById(R.id.tv_fileOpenTime);
            fileTypeImage_iv = view.findViewById(R.id.iv_fileTypeIcon);
            editButton_ib = view.findViewById(R.id.image_btn_edit);
            deleteButton_ib = view.findViewById(R.id.image_btn_delete);

        }

        public void setDataForViews(final Cursor c) {

            final String fileName = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
            final String fileOpenTime = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME));
            final String fileContent = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT));
            final String fileType = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
            final String fileUri = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI));
            final String fileID = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry._ID));

            fileTitle_tv.setText(fileName);
            fileOpenTime_tv.setText(fileOpenTime);

            editButton_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    renameFileNameDialog(fileTitle_tv, fileID);

                }
            });

            deleteButton_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuild = new AlertDialog.Builder(mContext);

                    alertBuild.setTitle(R.string.delete_dialog_title)
                            .setMessage(String.format(mContext.getString(R.string.delete_dialog_message),
                                    mDocumentList.get(Integer.parseInt(fileID)-1).title));

                    alertBuild.setPositiveButton(R.string.delete_dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFiles(fileID);
                        }
                    });

                    alertBuild.setNegativeButton(R.string.delete_dialog_negatibe_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Nothing happens, just close dialog
                        }
                    });
                    AlertDialog dialog = alertBuild.create();
                    dialog.show();
                }
            });

            int typeImageId;
            switch (fileType) {
                case "pdf":
                    typeImageId = R.drawable.ic_pdf;
                    break;
                case "txt":
                    typeImageId = R.drawable.ic_txt;
                    break;
                case "doc":
                    typeImageId = R.drawable.ic_doc;
                    break;
                case "docx":
                    typeImageId = R.drawable.ic_doc;
                    break;
                case "ppt":
                    typeImageId = R.drawable.ic_ppt;
                    break;
                case "pptx":
                    typeImageId = R.drawable.ic_ppt;
                    break;
                case "rtf":
                    typeImageId = R.drawable.ic_rtf;
                    break;
                case "rtx":
                    typeImageId = R.drawable.ic_rtf;
                    break;
                default:
                    typeImageId = R.drawable.ic_unkown_file_type_icon;

            }
            fileTypeImage_iv.setImageResource(typeImageId);

            setDocumentData(c);
        }

        private void setDocumentData(Cursor c) {
            mDocument = new Document();
            mDocument.title = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
            mDocument.documentType = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
            mDocument.time = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME));
            mDocument.text = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT));
            mDocument.documentUri = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI));
        }
    }

    private void deleteFiles(String id) {
        String selection = DocumentContract.DocumentEntry._ID + " =?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(DocumentContract.DocumentEntry.CONTENT_URI, selection, selectionArgs);
        mListener.onFilesListenerInteraction();
    }

    private void renameFileNameDialog(final TextView tv, final String id) {
        final EditText editText = new EditText(mContext);

        editText.setSingleLine(true);
        editText.setText(tv.getText());

        int FileListIndex = Integer.parseInt(id);

        final Document selectedDocument = mDocumentList.get(FileListIndex-1);

        AlertDialog.Builder alertBuild = new AlertDialog.Builder(mContext);

        alertBuild.setTitle(R.string.edit_file_name_title)
                .setView(editText);

        alertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                tv.setText(newName);
                selectedDocument.title = newName;
                ContentValues fileValue = new ContentValues();
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME, selectedDocument.title);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE, selectedDocument.documentType);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME, selectedDocument.time);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT, selectedDocument.text);
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI, selectedDocument.documentUri);

                String selection = DocumentContract.DocumentEntry._ID + " =?";
                String[] selectionArgs = {id};

                mContext.getContentResolver().update(DocumentContract.DocumentEntry.CONTENT_URI, fileValue, selection, selectionArgs);
            }
        });

        alertBuild.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing happens, just close dialog
            }
        });
        AlertDialog dialog = alertBuild.create();
        dialog.show();

    }

    public interface OnEditOrDeleteFilesListener {
        void onFilesListenerInteraction();
    }
}

