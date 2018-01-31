package com.example.android.proteleprompter.Adaptor;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.setData(cursor);
        mDocumentList.add(mDocument);
    }

    private void openScrollingActivity(documentsListViewHolder vh){

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

        public documentsListViewHolder(View view) {
            super(view);
            fileTitle_tv = view.findViewById(R.id.tv_fileTitle);
            fileOpenTime_tv = view.findViewById(R.id.tv_fileOpenTime);
            fileTypeImage_iv = view.findViewById(R.id.iv_fileTypeIcon);

        }

        private void setDocumentData(Cursor c){
            mDocument = new Document();
            mDocument.title = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
            mDocument.documentType = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
            mDocument.time = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME));
            mDocument.text = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT));
            mDocument.documentUri = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_URI));
        }

        public void setData(Cursor c) {

            fileTitle_tv.setText(c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME)));
            fileOpenTime_tv.setText(c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME)));
            String fileType = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));

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

    }
}

