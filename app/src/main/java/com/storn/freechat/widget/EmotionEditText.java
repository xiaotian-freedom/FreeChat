package com.storn.freechat.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.storn.freechat.util.EmotionHelper;


public class EmotionEditText extends EditText {

    public EmotionEditText(Context context) {
        super(context);
    }

    public EmotionEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EmotionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            super.setText(EmotionHelper.replace(getContext(), text.toString(), 2, false), type);
        } else {
            super.setText(text, type);
        }
    }
}
