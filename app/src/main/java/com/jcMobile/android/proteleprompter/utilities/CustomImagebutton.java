package com.jcMobile.android.proteleprompter.utilities;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomImagebutton extends android.support.v7.widget.AppCompatImageButton{


    public CustomImagebutton(Context context) {
        super(context);
    }

    public CustomImagebutton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImagebutton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            this.setImageAlpha((int) (0.65 * 255));
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            this.setImageAlpha((int) (1.0 * 255));
        }

        return super.onTouchEvent(e);

    }


}
