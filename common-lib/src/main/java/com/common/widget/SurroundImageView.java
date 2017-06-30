package com.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.common.R;

/**
 * 带环形边框的ImageView
 *
 * @author Administrator
 */
public class SurroundImageView extends AppCompatImageView {

    private int mBorderThickness = 0;
    private Context mContext;
    private int mBorderColor = 0xFFFFFFFF;

    private Paint mPaint;

    public SurroundImageView(Context context) {
        super(context);
        mContext = context;
        mPaint = new Paint();
    }

    public SurroundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        setCustomAttributes(attrs);
    }

    public SurroundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mPaint = new Paint();
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.SurroundImageView);
        mBorderThickness = a.getDimensionPixelSize(
                R.styleable.SurroundImageView_borderThickness, 0);
        mBorderColor = a.getColor(R.styleable.SurroundImageView_borderColor,
                mBorderColor);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        int radius = (w < h ? w : h) / 2 - mBorderThickness;
        Bitmap roundBitmap = getCroppedBitmap(bitmap, radius);
        // roundBitmap=ImageUtils.setCircularInnerGlow(roundBitmap, 0xFFBAB399,
        // 4, 1);
        // canvas.drawBitmap(roundBitmap, w / 2 - radius, 8, null);

        // final Paint paint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mPaint.setColor(mBorderColor);
        canvas.drawCircle(w / 2, h / 2, radius + mBorderThickness, mPaint);
        canvas.drawBitmap(roundBitmap, w / 2 - radius, h / 2 - radius, null);

    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        if (bmp.getWidth() != diameter || bmp.getHeight() != diameter)
            scaledSrcBmp = Bitmap.createScaledBitmap(bmp, diameter, diameter,
                    false);
        else
            scaledSrcBmp = bmp;
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.GRAY);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);

        return output;
    }

}
