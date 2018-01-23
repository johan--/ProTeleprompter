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

import java.util.List;

public class DocumentAdaptor extends CursorRecyclerViewAdapter {

    private List<Document> mDocumentList;

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
                Document document = mDocumentList.get(vh.getAdapterPosition());

                Class mScrollActivity = ScrollActivity.class;

                Intent startScrollActivityIntent = new Intent(mContext, mScrollActivity);

                Bundle documentBundle = new Bundle();

                documentBundle.putSerializable("document", document);

                startScrollActivityIntent.putExtra("bundle", documentBundle);

                ActivityCompat.startActivity(mContext, startScrollActivityIntent, null);
            }
        });
        return vh;
    }

//    @Override
//    public void onBindViewHolder(documentsListViewHolder holder, int position) {
//        //RecyclerviewListContentBinding contentBinding = holder.mListContentBinding;
//        holder.bindDocument(mDocumentList.get(position));
//        //contentBinding.setViewModel(new DocumentDetailsViewModel(mContext,mDocumentList.get(position)));
//    }


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
    }

//    @Override
//    public void setCursor(Cursor newCursor) {
//        super.setCursor(newCursor);
//        notifyDataSetChanged();
//    }

//    public void setDocumentList(List<Document> documentList) {
//        this.mDocumentList = documentList;
//        notifyDataSetChanged();
//    }

    public static class documentsListViewHolder extends RecyclerView.ViewHolder {

        TextView fileTitle_tv;
        TextView fileOpenTime_tv;
        ImageView fileTypeImage_iv;

        public documentsListViewHolder(View view) {
            super(view);
            fileTitle_tv = view.findViewById(R.id.tv_fileTitle);
            fileOpenTime_tv = view.findViewById(R.id.tv_fileOpenTime);
            fileTypeImage_iv = view.findViewById(R.id.iv_fileTypeIcon);

        }

        public void setData(Cursor c) {
            fileTitle_tv.setText(c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME)));
            fileOpenTime_tv.setText(c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME)));

            String fileType = c.getString(c.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
            int typeImageid;
            switch (fileType) {
                case ".pdf":
                    typeImageid = R.drawable.ic_pdf;
                    break;
                case ".txt":
                    typeImageid = R.drawable.ic_txt;
                    break;
                case ".doc":
                    typeImageid = R.drawable.ic_doc;
                    break;
                case ".docx":
                    typeImageid = R.drawable.ic_doc;
                    break;
                case ".ppt":
                    typeImageid = R.drawable.ic_ppt;
                    break;
                case ".pptx":
                    typeImageid = R.drawable.ic_ppt;
                    break;
                case ".rtf":
                    typeImageid = R.drawable.ic_rtf;
                    break;
                case ".rtx":
                    typeImageid = R.drawable.ic_rtf;
                    break;
                default:
                    typeImageid = R.drawable.ic_unkown_file_type_icon;

            }
            fileTypeImage_iv.setImageResource(typeImageid);
        }

//        public void bindDocument(Document document) {
//            if (mListContentBinding.getViewModel() == null) {
//                mListContentBinding.setViewModel(new DocumentDetailsViewModel(itemView.getContext(), document));
//            } else {
//                mListContentBinding.getViewModel().setDocument(document);
//            }
//        }
    }
}

