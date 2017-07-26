package com.common.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件工具类
 * Created by Stefen on 2016/8/3.
 */
public class FileUtil {

    public static final String APP_FOLDER_NAME = "free_chat";

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
        String path;
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
     * 检测文件目录
     *
     * @param dir
     * @return
     */
    public static String checkAndMkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public static String getChatFileDir() {
        String dir = getAppExternalCacheDir("files").getAbsolutePath();
        return checkAndMkdirs(dir);
    }

    /**
     * 获取录音文件路径
     *
     * @return
     */
    public static String getRecordTmpPath() {
        String recordPath = getChatFileDir();
        File file = new File(recordPath, "record_" + TimeUtil.getCurrentTime() + ".amr");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
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
     *
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

    /**
     * 获取缓存大小
     *
     * @return
     */
    public static double getCacheFileSize(Context context) {
        return calculateCacheFileSize(getAppExternalCacheDir(APP_TEMP_FOLDER_NAME)) + calculateCacheFileSize(context.getCacheDir());
    }

    /**
     * 递归缓存总大小
     *
     * @param file
     * @return
     */
    private static double calculateCacheFileSize(File file) {
        double size = 0;
        try {
            if (file.isDirectory()) {
                java.io.File[] fileList = file.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (File f : fileList) {
                        if (f.isDirectory()) {
                            size = size + calculateCacheFileSize(f);
                        } else {
                            size = size + f.length();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 清除所有缓存
     *
     * @param context
     * @return
     */
    public static boolean clearAllCache(Context context) {
        return cleanInnerCache(context) && cleanExternalCache();
    }

    /**
     * 清除应用内部缓存
     *
     * @param context
     * @return
     */
    private static boolean cleanInnerCache(Context context) {
        return recursionDeleteFile(context.getCacheDir());
    }

    /**
     * 清除应用外部缓存
     */
    private static boolean cleanExternalCache() {
        File cacheFile = getAppExternalCacheDir(APP_TEMP_FOLDER_NAME);
        return cacheFile.exists() && recursionDeleteFile(cacheFile);
    }

    /**
     * 递归删除文件
     *
     * @param file
     */
    private static boolean recursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return true;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return true;
            }
            for (File f : childFiles) {
                recursionDeleteFile(f);
            }
        }
        return true;
    }

    /**
     * 格式化单位
     *
     * @param size
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

}
