package com.example.android.proteleprompter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.android.proteleprompter.Data.Document;

public class ScrollActivity extends AppCompatActivity {

    private Document mDocument;
    private String mDocumentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        mDocument = (Document) this.getIntent().getBundleExtra("bundle").getSerializable("document");
        if(mDocument !=null) {
            int dotIndex = mDocument.title.lastIndexOf(".");
            mDocumentTitle = mDocument.title.substring(0, dotIndex);
        }

        ActionBar actionBar = getSupportActionBar();

        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textViewTitle =  viewActionBar.findViewById(R.id.toolBar_title);
        textViewTitle.setText(mDocumentTitle);
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}
