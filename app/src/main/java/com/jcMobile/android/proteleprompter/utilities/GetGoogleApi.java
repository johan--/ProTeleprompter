package com.jcMobile.android.proteleprompter.utilities;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class GetGoogleApi extends Activity implements EasyPermissions.PermissionCallbacks {

    private final Context mContext;


    public GetGoogleApi(Context context) {
        this.mContext = context;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


}
