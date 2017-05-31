package com.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap 工具
 * Created by Stefen on 2016/7/26.
 */
public class BitmapUtil {

    private static final int REQUEST_WIDTH = 1280;
    private static final int REQUEST_HEIGHT = 800;
    private static final int HD_PHOTO_SIZE = 1024 * 1024;

    private static final String SHARE_LOGO_ASSETS_PATH = "image/logo.png";
    private static final String SHARE_LOGO_IMAGE_NAME = "share_logo.png";

    /**
     * 高斯模糊
     *
     * @param bkg         Bitmap 需要模糊的Bitmap
     * @param view        View 需要设置模糊图像的View
     * @param scaleFactor int 缩放因子
     * @param radius      int 模糊程度
     */
    @TargetApi(16)
    public static void blur(Bitmap bkg, View view, int scaleFactor, int radius) {
        long startMs = System.currentTimeMillis();
        int width = view.getMeasuredWidth() / scaleFactor;
        int height = view.getMeasuredHeight() / scaleFactor;
        Bitmap overlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, radius, true);
        Resources res = view.getResources();
        int sdk_int = Build.VERSION.SDK_INT;
        if (sdk_int >= 16) {
            view.setBackground(new BitmapDrawable(res, overlay));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(res, overlay));
        }
        LogUtil.e("Gauss Blur", System.currentTimeMillis() - startMs + "ms");
    }

    /**
     * 压缩图片
     *
     * @param srcFile
     * @return
     */
    public static File compressPhoto(File srcFile) {
        return compressPhoto(srcFile, srcFile.getName(), REQUEST_WIDTH,
                REQUEST_HEIGHT);
    }

    /**
     * @param srcFile       原始图片文件
     * @param fileName      原始图片名字
     * @param requestWidth  期望宽度
     * @param requestHeight 期望高度
     * @return 压缩后图片File
     */
    public static File compressPhoto(File srcFile, String fileName,
                                     int requestWidth, int requestHeight) {
        ByteArrayOutputStream baos = null;
        FileOutputStream fos;
        Bitmap bmpSample;

        try {
            if (srcFile.length() < HD_PHOTO_SIZE) {
                return srcFile;
            }

            File curFileDir = FileUtil.getAppExternalCacheDir(FileUtil.APP_TEMP_FOLDER_NAME);
            if (!curFileDir.exists()) {
                curFileDir.mkdirs();
            }
            File tmpFile = new File(curFileDir, fileName);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            bmpSample = decodeSampledBitmapFromFile(srcFile.getPath(),
                    requestWidth, requestHeight);
            if (tmpFile.createNewFile()) {
                baos = new ByteArrayOutputStream();
                bmpSample.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                fos = new FileOutputStream(tmpFile);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                return tmpFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 转换图片路径为bitmap
     *
     * @param filename String
     * @return Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        Bitmap bmp;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(filename, options);
        return bmp;
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and
     * height.
     *
     * @param filename  The full path of the file to decode
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect
     * ratio and dimensions that are equal to or greater than the
     * requested width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        Bitmap bmp;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(filename, options);
        return bmp;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
     * object when decoding bitmaps using the decode* methods from
     * {@link BitmapFactory}. This implementation calculates the closest
     * inSampleSize that will result in the final decoded bitmap having a width
     * and height equal to or larger than the requested width and height. This
     * implementation does not ensure a power of 2 is returned for inSampleSize
     * which can be faster when decoding but results in a larger bitmap which
     * isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run
     *                  through a decode* method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee a final image
            // with both dimensions larger than or equal to the requested height
            // and width.

            if (heightRatio < widthRatio) {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }

        return inSampleSize;
    }

    /**
     * 转换bitmap为字节数组
     *
     * @param bmp         Bitmap
     * @param needRecycle boolean
     * @return byte[]
     */
    public static byte[] bitmapToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle) {
                bmp.recycle();
            }
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                e.printStackTrace();
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * 获取Assets下的logo bitmap
     *
     * @param context Context
     * @return bitmap
     */
    public static Bitmap getLogoBitmap(Context context) {
        Bitmap bmpLogo = null;
        File diskCacheDir = FileUtil.getAppExternalCacheDir(FileUtil.APP_IMAGE_FOLDER_NAME);
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdir();
        }
        File logoFile = FileUtil.getDiskFile(FileUtil.APP_IMAGE_FOLDER_PATH, SHARE_LOGO_IMAGE_NAME);

        try {
            if (!logoFile.exists()) {
                logoFile.createNewFile();
                AssetManager am = context.getAssets();
                InputStream is = am.open(SHARE_LOGO_ASSETS_PATH);
                bmpLogo = BitmapFactory.decodeStream(is);
                FileOutputStream fos = new FileOutputStream(logoFile);
                bmpLogo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                is.close();
            } else {
                bmpLogo = BitmapFactory.decodeFile(logoFile.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmpLogo;
    }

    /**
     * @param drawable drawable 转 Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
