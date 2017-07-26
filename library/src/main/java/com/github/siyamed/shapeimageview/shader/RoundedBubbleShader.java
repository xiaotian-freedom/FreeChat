package com.github.siyamed.shapeimageview.shader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.R;

/**
 * 带有bubble的圆形shader
 * Created by tianshutong on 2017/7/12.
 */

public class RoundedBubbleShader extends ShaderHelper {

    private static final int DEFAULT_HEIGHT_DP = 10;
    private static final int DEFAULT_BUBBLE_Y = 30;
    private final RectF imageRect = new RectF();
    private final Path path = new Path();

    private int triangleHeightPx;
    private int bubbleY;
    private BubbleShader.ArrowPosition arrowPosition = BubbleShader.ArrowPosition.LEFT;

    private int radius = 0;
    private int bitmapRadius;

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        borderWidth = 0;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            radius = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_siRadius, radius);
            triangleHeightPx = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_siTriangleHeight, 0);
            int arrowPositionInt = typedArray.getInt(R.styleable.ShaderImageView_siArrowPosition, BubbleShader.ArrowPosition.LEFT.ordinal());
            arrowPosition = BubbleShader.ArrowPosition.values()[arrowPositionInt];
            bubbleY = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_siBubbleY, DEFAULT_BUBBLE_Y);
            typedArray.recycle();
        }
        if (triangleHeightPx == 0) {
            triangleHeightPx = dpToPx(context.getResources().getDisplayMetrics(), DEFAULT_HEIGHT_DP);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        canvas.save();
        canvas.concat(matrix);
        canvas.drawRoundRect(imageRect, bitmapRadius, bitmapRadius, imagePaint);
        canvas.drawPath(path, imagePaint);
        canvas.restore();
    }

    @Override
    public void onSizeChanged(int width, int height) {
        super.onSizeChanged(width, height);
    }

    @Override
    public void reset() {
        path.reset();
        imageRect.set(0, 0, 0, 0);
        bitmapRadius = 0;
    }

    @Override
    public void calculate(int bitmapWidth, int bitmapHeight, float width, float height, float scale, float translateX, float translateY) {

        path.reset();
        float x = -translateX;
        float y = -translateY;
        float scaledTriangleHeight = triangleHeightPx / scale;
        float resultWidth = bitmapWidth + 2 * translateX;
        float resultHeight = bitmapHeight + 2 * translateY;
        float centerY = resultHeight / 2f + y;

        path.setFillType(Path.FillType.EVEN_ODD);
        float rectLeft;
        float rectRight;
        switch (arrowPosition) {
            case LEFT:
                rectLeft = scaledTriangleHeight + x;
                rectRight = resultWidth + rectLeft;
                path.addRect(rectLeft, y, rectRight, resultHeight + y, Path.Direction.CW);

                path.moveTo(x, centerY);
                path.lineTo(rectLeft, centerY - scaledTriangleHeight);
                path.lineTo(rectLeft, centerY + scaledTriangleHeight);
                path.lineTo(x, centerY);

                imageRect.set(rectLeft, y, resultWidth + rectLeft, resultHeight);

                break;
            case RIGHT:
                rectLeft = x;
                float imgRight = resultWidth + rectLeft;
                rectRight = imgRight - scaledTriangleHeight;
                path.addRect(rectLeft, y, rectRight, resultHeight + y, Path.Direction.CW);
                path.moveTo(imgRight, centerY);
                path.lineTo(rectRight, centerY - scaledTriangleHeight);
                path.lineTo(rectRight, centerY + scaledTriangleHeight);
                path.lineTo(imgRight, centerY);

                imageRect.set(rectLeft, y, resultWidth + rectLeft, resultHeight);

                break;
        }

        bitmapRadius = Math.round(radius / scale);
    }


    public int getTriangleHeightPx() {
        return triangleHeightPx;
    }

    public void setTriangleHeightPx(final int triangleHeightPx) {
        this.triangleHeightPx = triangleHeightPx;
    }

    public BubbleShader.ArrowPosition getArrowPosition() {
        return arrowPosition;
    }

    public void setArrowPosition(final BubbleShader.ArrowPosition arrowPosition) {
        this.arrowPosition = arrowPosition;
    }

    public int getBubbleY() {
        return bubbleY;
    }

    public void setBubbleY(int bubbleY) {
        this.bubbleY = bubbleY;
    }

    public final int getRadius() {
        return radius;
    }

    public final void setRadius(final int radius) {
        this.radius = radius;
    }
}
