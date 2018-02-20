package com.jcMobile.android.proteleprompter.adaptor;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcMobile.android.proteleprompter.contentprovider.DocumentContract;
import com.jcMobile.android.proteleprompter.data.Document;
import com.jcMobile.android.proteleprompter.R;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdaptor extends CursorRecyclerViewAdapter {

    public final List<Document> mDocumentList = new ArrayList<>();

    private Document mDocument;

    private final Context mContext;

    private OnEditOrDeleteFilesListener mListener;

    public DocumentAdaptor(Context context) {
        super(context, null);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_document, parent, false);

        return new documentsListViewHolder(view);
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
        int position  = cursor.getPosition();
        cursor.moveToPosition(position);
        holder.setDataForViews(position,cursor);
        mDocumentList.add(mDocument);
    }

    public void setOnEditFilesListener(OnEditOrDeleteFilesListener listener) {
        mListener = listener;
    }

    public class documentsListViewHolder extends RecyclerView.ViewHolder {

        final TextView fileTitle_tv;
        final TextView fileOpenTime_tv;
        final ImageView fileTypeImage_iv;
        final ImageButton editButton_ib;
        final ImageButton deleteButton_ib;

        public documentsListViewHolder(View view) {
            super(view);
            fileTitle_tv = view.findViewById(R.id.tv_fileTitle);
            fileOpenTime_tv = view.findViewById(R.id.tv_fileOpenTime);
            fileTypeImage_iv = view.findViewById(R.id.iv_fileTypeIcon);
            editButton_ib = view.findViewById(R.id.image_btn_edit);
            deleteButton_ib = view.findViewById(R.id.image_btn_delete);

        }

        public void setDataForViews(final int position, final Cursor c) {

            final String fileName = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
            final String fileOpenTime = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME));
            final String fileType = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
            final String fileID = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry._ID));

            fileTitle_tv.setText(fileName);
            fileOpenTime_tv.setText(fileOpenTime);

            editButton_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    renameFileNameDialog(position,fileTitle_tv, fileID);

                }
            });

            deleteButton_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuild = new AlertDialog.Builder(mContext);

                    alertBuild.setTitle(R.string.delete_dialog_title)
                            .setMessage(String.format(mContext.getString(R.string.delete_dialog_message),
                                    mDocumentList.get(position).title));

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
            fileTypeImage_iv.setTag(fileID);

            setDocumentData(c);
        }

        private void setDocumentData(Cursor c) {

            String uri = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI));
            String time = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME));
            String title = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
            String text = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT));
            String type = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
            mDocument = new Document(uri, time, title, text, type);

        }
    }

    private void deleteFiles(String id) {
        String selection = DocumentContract.DocumentEntry._ID + " =?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(DocumentContract.DocumentEntry.CONTENT_URI, selection, selectionArgs);
        mListener.onFilesListenerInteraction();
    }

    //TODO: dialog would dismiss when rotating, Dialog fragment is supposed to be used
    private void renameFileNameDialog(int position, final TextView tv, final String id) {
        final EditText editText = new EditText(mContext);

        editText.setSingleLine(true);
        editText.setText(tv.getText());

        final Document selectedDocument = mDocumentList.get(position);

        AlertDialog.Builder alertBuild = new AlertDialog.Builder(mContext);

        alertBuild.setTitle(R.string.edit_dialog_rename_title)
                .setView(editText);

        alertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                tv.setText(newName);
                selectedDocument.title = newName;
                ContentValues fileValue = new ContentValues();
                fileValue.put(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME, newName);


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

