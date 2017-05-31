package com.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.R;

/**
 * 提示对话框
 * Created by Stefen on 2016/7/19.
 */
public class XZAlertDialog extends Dialog {

    private static final int BUTTON_CANCEL = 1;

    public XZAlertDialog(Context context) {
        super(context);
    }

    public XZAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public XZAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context mContext;
        private TextView tvTitle;
        private ImageButton ibRight;

        private View view;
        private String title;
        private Drawable ibRightSrc;
        private float mDimAmount = 0.6f;
        private float mWidthRate = 0.87F;
        private float mHeightRate = 0.0f;

        private DialogInterface.OnClickListener mOnIBRightClickListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(int title) {
            this.title = mContext.getText(title).toString();
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        @SuppressWarnings("NewApi")
        public Builder setIBRight(int resId) {
            Resources res = mContext.getResources();
            int sdk_int = Build.VERSION.SDK_INT;
            if (sdk_int >= 21) {
                ibRightSrc = res.getDrawable(resId, null);
            } else {
                ibRightSrc = res.getDrawable(resId);
            }
            return this;
        }

        public Builder setIBRightClickListener(DialogInterface.OnClickListener listener) {
            this.mOnIBRightClickListener = listener;
            return this;
        }

        public Builder setView(int layoutResId) {
            view = LayoutInflater.from(mContext).inflate(layoutResId, null, false);
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setWidthRate(float rate) {
            this.mWidthRate = rate;
            return this;
        }

        public Builder setHeightRate(float rate) {
            this.mHeightRate = rate;
            return this;
        }

        public Builder setDimAmount(float dimAmount) {
            this.mDimAmount = dimAmount;
            return this;
        }

        public XZAlertDialog create() {
            final XZAlertDialog dialog = new XZAlertDialog(mContext, R.style.BaseDialog);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.xz_alert_dialog_layout, null, false);

            tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }

            ibRight = (ImageButton) layout.findViewById(R.id.ibRight);
            if (ibRightSrc != null) {
                ibRight.setImageDrawable(ibRightSrc);
            }
            ibRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ibRightSrc != null && mOnIBRightClickListener != null) {
                        mOnIBRightClickListener.onClick(dialog, BUTTON_CANCEL);
                    } else {
                        dialog.dismiss();
                    }
                }
            });

            LinearLayout llContent = (LinearLayout) layout.findViewById(R.id.llContent);
            if (null != view) {
                llContent.removeAllViews();
                llContent.addView(view);
            }

            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            Window dialogWindow = dialog.getWindow();
            DisplayMetrics dm = new DisplayMetrics();
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (dm.widthPixels * mWidthRate);
            if (mHeightRate > 0.0f) {
                lp.height = (int) (dm.heightPixels * mHeightRate);
            } else {
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            lp.dimAmount = mDimAmount;
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);

            return dialog;
        }

    }

}
