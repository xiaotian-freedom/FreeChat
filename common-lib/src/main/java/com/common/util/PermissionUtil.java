package com.common.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.common.common.Constants;

/**
 * 权限判断
 * Created by tianshutong on 16/3/30.
 */
public class PermissionUtil {

    static PackageManager packageManager;

    public PermissionUtil() {

    }

    public static boolean isReadCamera(Context mContext) {
//        packageManager = mContext.getPackageManager();
//        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.CAMERA", Constants.APP_PACKAGE_NAME);
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isReadStorage(Context mContext) {
//        packageManager = mContext.getPackageManager();
//        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.READ_EXTERNAL_STORAGE", Constants.APP_PACKAGE_NAME);
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isWriteStorage(Context mContext) {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isReadRecord(Context mContext) {
        int permission = mContext.checkCallingOrSelfPermission("android.permission.RECORD_AUDIO");
        return permission == PackageManager.PERMISSION_GRANTED;
//        packageManager = mContext.getPackageManager();
//        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.RECORD_AUDIO", Constants.APP_PACKAGE_NAME);
    }

    public static boolean isReadContact(Context mContext) {
        packageManager = mContext.getPackageManager();
        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.READ_CONTACTS", Constants.APP_PACKAGE_NAME);
    }

    public static boolean isReadPhone(Context mContext) {
//        packageManager = mContext.getPackageManager();
//        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.READ_PHONE_STATE", Constants.APP_PACKAGE_NAME);
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCallPhone(Context mContext) {
//        packageManager = mContext.getPackageManager();
//        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.CALL_PHONE", Constants.APP_PACKAGE_NAME);
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCoaseLocation(Context mContext) {
//        packageManager = mContext.getPackageManager();
//        boolean isGranted = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", Constants.APP_PACKAGE_NAME);
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isInternet(Context mContext) {
//        packageManager = mContext.getPackageManager();
//        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.INTERNET", Constants.APP_PACKAGE_NAME);
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions,requestCode);
    }

}
