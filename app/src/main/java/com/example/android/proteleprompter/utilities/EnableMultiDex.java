package com.example.android.proteleprompter.utilities;

import android.content.Context;
import android.support.multidex.MultiDexApplication;


public class EnableMultiDex extends MultiDexApplication {
    private  EnableMultiDex enableMultiDex;
    private Context context;

    public EnableMultiDex(){
        enableMultiDex=this;
    }

    public  EnableMultiDex getEnableMultiDexApp() {
        return enableMultiDex;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }
}
