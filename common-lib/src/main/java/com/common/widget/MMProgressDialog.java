package com.common.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common.R;

public class MMProgressDialog extends Dialog {

    private ProgressBar mProgress;
    private TextView mMessageView;

    private CharSequence mMessage;
    private boolean mIndeterminate;

    public MMProgressDialog(Context context) {
        super(context, R.style.MMProgressDialog);
    }

    public MMProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public MMProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static MMProgressDialog show(Context context, CharSequence message) {
        return show(context, message, false);
    }

    public static MMProgressDialog show(Context context, CharSequence message, boolean indeterminate) {
        return show(context, message, indeterminate, false, null);
    }

    public static MMProgressDialog show(Context context, CharSequence message, boolean indeterminate,
                                        boolean cancelable) {
        return show(context, message, indeterminate, cancelable, null);
    }

    public static MMProgressDialog show(Context context, CharSequence message, boolean indeterminate,
                                        boolean cancelable, OnCancelListener cancelListener) {
        MMProgressDialog dialog = new MMProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.mm_progress_dialog, null);
        mProgress = (ProgressBar) layout.findViewById(R.id.mm_progress_dialog_icon);
        mMessageView = (TextView) layout.findViewById(R.id.mm_progress_dialog_msg);
        setContentView(layout);
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);

        super.onCreate(savedInstanceState);
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }

    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            mMessageView.setText(message);
        } else {
            mMessage = message;
        }
    }

    public void setMessage(int messageId) {
        mMessage = getContext().getResources().getString(messageId);
        if (mProgress != null) {
            mMessageView.setText(mMessage);
        }
    }

    public void hideProgress() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
            mMessageView.setGravity(Gravity.CENTER);
        }
    }

}
