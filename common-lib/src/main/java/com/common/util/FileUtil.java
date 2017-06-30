package com.common.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件工具类
 * Created by Stefen on 2016/8/3.
 */
public class FileUtil {

    public static final String APP_FOLDER_NAME = "free_chat";

    public static final String APP_IMAGE_FOLDER_NAME = "image";
    public static final String APP_IMAGE_FOLDER_PATH = APP_FOLDER_NAME
            + File.separator + APP_IMAGE_FOLDER_NAME;

    public static final String APP_TEMP_FOLDER_NAME = "temp";
    public static final String APP_TEMP_FOLDER_PATH = APP_FOLDER_NAME
            + File.separator + APP_TEMP_FOLDER_NAME;

    public static final String APP_DATA_FOLDER_NAME = "data";
    public static final String APP_DATA_FOLDER_PATH = APP_FOLDER_NAME
            + File.separator + APP_DATA_FOLDER_NAME;

    /**
     * 获取App目录下Temp文件绝对路径
     *
     * @param fileName 文件名
     * @return file
     */
    public static File getAppTempFile(String fileName) {
        return getDiskFile(APP_TEMP_FOLDER_PATH, fileName);
    }

    /**
     * 获取SD Card中App目录下的缓存文件夹
     *
     * @param dirName 缓存文件夹的名称
     * @return file
     */
    public static File getAppExternalCacheDir(String dirName) {
        final String cacheDir = File.separator + APP_FOLDER_NAME
                + File.separator + dirName + File.separator;
        String path = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = Environment.getDataDirectory().getPath();
        }
        return new File(path + cacheDir);
    }

    /**
     * 获取SD Card中App目录下的缓存文件
     *
     * @param dirName    缓存文件所在的文件夹名称
     * @param uniqueName 缓存文件夹名称
     * @return File
     */
    public static File getDiskFile(String dirName, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        final String cachePath;

        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            cachePath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        } else {
            cachePath = Environment.getDataDirectory() + File.separator + dirName;
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 以时间戳作为文件名
     *
     * @param strFormat String 时间戳格式
     * @return String
     */
    public static String getFileNameByTimeStamp(String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取App目录下Image文件绝对路径
     *
     * @param fileName 图片文件名
     * @return file
     */
    public static File getAppImageFile(String fileName) {
        return getDiskFile(APP_IMAGE_FOLDER_PATH, fileName);
    }

    /**
     * 获取App目录下Data文件绝对路径
     *
     * @param fileName 文件名
     * @return file
     */
    public static File getAppDataFile(String fileName) {
        return getDiskFile(APP_DATA_FOLDER_PATH, fileName);
    }

    /**
     * 生成相机文件
     *
     * @return
     */
    public static File generateCameraFile() {
        File mCurPhotoFile;
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File sdDir = Environment.getExternalStorageDirectory();
        File saveDir = new File(sdDir.toString() + "/DCIM/Camera");

        String strFileName = FileUtil.getFileNameByTimeStamp("yyyyMMdd_kkmmss") + ".jpg";
        mCurPhotoFile = new File(saveDir, strFileName);
        if (mCurPhotoFile.exists()) {
            mCurPhotoFile.deleteOnExit();
        }
        try {
            if (mCurPhotoFile.createNewFile()) return mCurPhotoFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取contentUri
     * @param context
     * @param imageFile
     * @return
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
