package com.jcMobile.android.proteleprompter.data;

import android.os.Parcel;
import android.os.Parcelable;


public class Document implements Parcelable {

    private final String documentUri;
    private final String time;
    public String title;
    private final String text;
    private final String documentType;

    private Document(Parcel in) {
        documentUri = in.readString();
        time = in.readString();
        title = in.readString();
        text = in.readString();
        documentType = in.readString();
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    public Document(String documentUri, String time, String title, String text, String documentType) {
        this.documentUri = documentUri;
        this.time = time;
        this.title = title;
        this.text = text;
        this.documentType = documentType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentUri);
        dest.writeString(time);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(documentType);
    }
}
