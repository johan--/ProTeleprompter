package com.example.android.proteleprompter.Adaptor;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.R;

public class NewDocumentAdaptor extends
        RecyclerView.Adapter<NewDocumentAdaptor.VH> {

    private Cursor mCursor;
    private adaptorListener mListener;

    public NewDocumentAdaptor(adaptorListener listener) {
        mListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);

        final VH vh = new VH(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
                mCursor.moveToPosition(position);
                if (mListener != null) mListener.itemListener(mCursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mCursor.moveToPosition(position);

        holder.fileTitle_tv.setText(mCursor.getString(mCursor.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME)));
        holder.fileOpenTime_tv.setText(mCursor.getString(mCursor.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME)));

        String fileType = mCursor.getString(mCursor.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE));
        int typeImageid;
//        switch (fileType) {
//            case ".pdf":
//                typeImageid = R.drawable.ic_pdf;
//                break;
//            case ".txt":
//                typeImageid = R.drawable.ic_txt;
//                break;
//            case ".doc":
//                typeImageid = R.drawable.ic_doc;
//                break;
//            case ".docx":
//                typeImageid = R.drawable.ic_doc;
//                break;
//            case ".ppt":
//                typeImageid = R.drawable.ic_ppt;
//                break;
//            case ".pptx":
//                typeImageid = R.drawable.ic_ppt;
//                break;
//            case ".rtf":
//                typeImageid = R.drawable.ic_rtf;
//                break;
//            case ".rtx":
//                typeImageid = R.drawable.ic_rtf;
//                break;
//            default:
//                typeImageid = R.drawable.ic_unkown_file_type_icon;

        //holder.fileTypeImage_iv.setImageResource(typeImageid);

    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                int idx_id = mCursor.getColumnIndex(DocumentContract.DocumentEntry._ID);
                return mCursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface adaptorListener {
        void itemListener(Cursor cursor);
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView fileTitle_tv;
        TextView fileOpenTime_tv;
        //ImageView fileTypeImage_iv;

        public VH(View v) {
            super(v);
            fileTitle_tv = v.findViewById(R.id.tv_fileTitle);
            fileOpenTime_tv = v.findViewById(R.id.tv_fileOpenTime);
            //fileTypeImage_iv = v.findViewById(R.id.iv_fileTypeIcon);
        }
    }
}
