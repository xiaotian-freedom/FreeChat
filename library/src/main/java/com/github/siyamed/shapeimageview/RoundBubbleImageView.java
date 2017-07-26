package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.shader.BubbleShader;
import com.github.siyamed.shapeimageview.shader.RoundedBubbleShader;
import com.github.siyamed.shapeimageview.shader.ShaderHelper;

/**
 * 带有bubble的圆形图片
 * Created by tianshutong on 2017/7/12.
 */

public class RoundBubbleImageView extends ShaderImageView {

    private RoundedBubbleShader shader;

    public RoundBubbleImageView(Context context) {
        super(context);
    }

    public RoundBubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundBubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        shader = new RoundedBubbleShader();
        return shader;
    }

    public int getTriangleHeightPx() {
        if (shader != null) {
            return shader.getTriangleHeightPx();
        }
        return 0;
    }

    public void setTriangleHeightPx(final int triangleHeightPx) {
        if (shader != null) {
            shader.setTriangleHeightPx(triangleHeightPx);
            invalidate();
        }
    }

    public BubbleShader.ArrowPosition getArrowPosition() {
        if (shader != null) {
            return shader.getArrowPosition();
        }

        return BubbleShader.ArrowPosition.LEFT;
    }

    public void setArrowPosition(final BubbleShader.ArrowPosition arrowPosition) {
        if (shader != null) {
            shader.setArrowPosition(arrowPosition);
            invalidate();
        }
    }

    public final int getBubbleY() {
        if (shader != null) {
            return shader.getRadius();
        }
        return 30;
    }

    public final void setBubbleY(final int bubbleY) {
        if (shader != null) {
            shader.setBubbleY(bubbleY);
            invalidate();
        }
    }

    public final int getRadius() {
        if (shader != null) {
            return shader.getRadius();
        }
        return 0;
    }

    public final void setRadius(final int radius) {
        if (shader != null) {
            shader.setRadius(radius);
            invalidate();
        }
    }
}
