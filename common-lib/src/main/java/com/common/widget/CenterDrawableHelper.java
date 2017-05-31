package com.common.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

public class CenterDrawableHelper {

    private static void onCenterDraw(TextView view, Canvas canvas, Drawable drawable, int gravity) {
        int drawablePadding = view.getCompoundDrawablePadding();
        int ratio = 1;
        float total;

        switch (gravity) {
            case Gravity.END:
                ratio = -1;
            case Gravity.START:
                total = view.getPaint().measureText(view.getText().toString())
                        + drawable.getIntrinsicWidth() + drawablePadding
                        + view.getPaddingLeft() + view.getPaddingRight();
                canvas.translate(ratio * (view.getWidth() - total) / 2, 0);
                break;
            case Gravity.BOTTOM:
                ratio = -1;
            case Gravity.TOP:
                Paint.FontMetrics fontMetrics = view.getPaint().getFontMetrics();
                total = fontMetrics.descent - fontMetrics.ascent
                        + drawable.getIntrinsicHeight() + drawablePadding
                        + view.getPaddingTop() + view.getPaddingBottom();
                canvas.translate(0, ratio * (view.getHeight() - total) / 2);
                break;
        }
    }

    public static void preDraw(TextView view, Canvas canvas) {
        Drawable[] drawables = view.getCompoundDrawables();
        if (drawables[0] != null) {
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            onCenterDraw(view, canvas, drawables[0], Gravity.START);
        } else if (drawables[1] != null) {
            view.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            onCenterDraw(view, canvas, drawables[1], Gravity.TOP);
        } else if (drawables[2] != null) {
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            onCenterDraw(view, canvas, drawables[2], Gravity.END);
        } else if (drawables[3] != null) {
            view.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            onCenterDraw(view, canvas, drawables[3], Gravity.BOTTOM);
        }
    }

}
