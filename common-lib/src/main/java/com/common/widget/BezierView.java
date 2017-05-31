package com.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tianshutong on 16/8/23.
 */

public class BezierView extends View {

    public static final int OVAL_RADIUS = 100;
    private static final PorterDuff.Mode MODE = PorterDuff.Mode.DST_OUT;
    private static int viewWidth = 0;
    private static int viewHeight = 0;
    private static int ovalHeight = 0;
    private static int screenHeight = 0;
    private static int startY = 0;

    private static Bitmap srcBitmap, dstBitmap;
    private PorterDuffXfermode porterDuffXfermode;

    public BezierView(Context context) {
        this(context, null);
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //创建一个PorterDuffXfermode对象
        porterDuffXfermode = new PorterDuffXfermode(MODE);
    }

    public static void setViewParams(int vw, int vh, int sh, int oh, int y) {
        viewWidth = vw;
        viewHeight = vh;
        ovalHeight = oh;
        screenHeight = sh;
        startY = y;
        if (viewWidth > 0 && viewHeight > 0 && ovalHeight > 0) {
            srcBitmap = makeSrc(viewWidth, ovalHeight);
            dstBitmap = makeDst(viewWidth, viewHeight);
        }
    }

    //创建一个矩形bitmap，作为dst图
    private static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        c.drawRect(0, 0, viewWidth, viewHeight, p);
        return bm;
    }

    // 创建一个弧形bitmap，作为src图
    private static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(0, 0);
        path.quadTo(viewWidth / 2, OVAL_RADIUS, viewWidth, 0);
        c.drawPath(path, p);
        return bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (srcBitmap == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setStyle(Paint.Style.FILL);

        //创建一个图层，在图层上演示图形混合后的效果
        int sc = canvas.saveLayer(0, 0, viewWidth, screenHeight, null, Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.drawBitmap(dstBitmap, 0, startY, paint);
        //设置Paint的Xfermode
        paint.setXfermode(porterDuffXfermode);
        canvas.drawBitmap(srcBitmap, 0, startY, paint);
        paint.setXfermode(null);
        // 还原画布
        canvas.restoreToCount(sc);
    }
}
