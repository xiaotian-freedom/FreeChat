package com.common.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.common.R;

public class ConfirmDialog extends Dialog {

    /**
     * The identifier for the positive button.
     */
    public static final int BUTTON_POSITIVE = -1;

    /**
     * The identifier for the negative button.
     */
    public static final int BUTTON_NEGATIVE = -2;

    /**
     * The identifier for the neutral button.
     */
    public static final int BUTTON_NEUTRAL = -3;

    public ConfirmDialog(Context context) {
        super(context);
    }

    public ConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    public ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * Helper class for creating a custom dialog
     **/
    public static class Builder {
        private Context mContext;

        private View customView;
        private TextView tvTitle;
        private Drawable msgDrawableLeft, msgDrawableRight,
                msgDrawableTop, msgDrawableBottom;

        private String title;
        private String message;
        private int messageColor = -1;
        private int messageSize = -1;
        private int paddingLeft = 0;
        private int paddingTop = 0;
        private int paddingRight = 0;
        private int paddingBottom = 0;
        private int contentPanelHeight = 0;
        private float dimAmount = 0.6f;
        private float mWidthRate = 0.87F;

        private boolean isBold = false;
        private boolean autoDismiss = true;

        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private String mNeutralButtonText;

        private OnClickListener mPositiveButtonListener;
        private OnClickListener mNegativeButtonListener;
        private OnClickListener mNeutralButtonListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title int
         * @return Builder
         */
        public Builder setTitle(int title) {
            this.title = mContext.getText(title).toString();
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title String
         * @return Builder
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setMessage(int contentId) {
            this.message = mContext.getText(contentId).toString();
            return this;
        }

        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setMessage(String content) {
            this.message = content;
            return this;
        }

        /**
         * 设置message字体颜色
         *
         * @param color int
         * @return Builder
         */
        public Builder setMessageColor(int color) {
            this.messageColor = color;
            return this;
        }

        public Builder setMessageTextSize(int size) {
            this.messageSize = size;
            return this;
        }

        public Builder setMessageTextBold() {
            isBold = true;
            return this;
        }

        public Builder setMessagePadding(int left, int top, int right, int bottom) {
            this.paddingLeft = left;
            this.paddingTop = top;
            this.paddingRight = right;
            this.paddingBottom = bottom;
            return this;
        }

        public Builder setMessageCompoundDrawable(Drawable left, Drawable top, Drawable right, Drawable bottom) {
            msgDrawableLeft = left;
            msgDrawableTop = top;
            msgDrawableRight = right;
            msgDrawableBottom = bottom;
            return this;
        }

        public Builder setMessageCompounDrawable(int left, int top, int right, int bottom) {
            Resources res = mContext.getResources();
            msgDrawableLeft = left != 0 ? res.getDrawable(left) : null;
            msgDrawableTop = top != 0 ? res.getDrawable(top) : null;
            msgDrawableRight = right != 0 ? res.getDrawable(right) : null;
            msgDrawableBottom = bottom != 0 ? res.getDrawable(bottom) : null;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog
         * is pressed.
         *
         * @param textId   The resource id of the text to display in the positive
         *                 button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            mPositiveButtonText = mContext.getText(textId).toString();
            mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog
         * is pressed.
         *
         * @param text     The text to display in the positive button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setPositiveButton(String text, final OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog
         * is pressed.
         *
         * @param textId   The resource id of the text to display in the negative
         *                 button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            mNegativeButtonText = mContext.getText(textId).toString();
            mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog
         * is pressed.
         *
         * @param text     The text to display in the negative button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setNegativeButton(String text, final OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is
         * pressed.
         *
         * @param textId   The resource id of the text to display in the neutral
         *                 button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setNeutralButton(int textId, final OnClickListener listener) {
            mNeutralButtonText = mContext.getText(textId).toString();
            mNeutralButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is
         * pressed.
         *
         * @param text     The text to display in the neutral button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setNeutralButton(String text, final OnClickListener listener) {
            mNeutralButtonText = text;
            mNeutralButtonListener = listener;
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog. If the supplied
         * view is an instance of a {@link ListView} the light background will
         * be used.
         *
         * @param view The view to use as the contents of the Dialog.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setCustomView(View view) {
            customView = view;
            return this;
        }

        public Builder setWidthRate(float rate) {
            mWidthRate = rate;
            return this;
        }

        public Builder setContentPanelHeight(int height) {
            contentPanelHeight = height;
            return this;
        }

        public Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        public Builder setAutoDismiss(boolean autoDismiss) {
            this.autoDismiss = autoDismiss;
            return this;
        }

        /**
         * Create the custom dialog
         */
        @SuppressLint("InflateParams")
        public ConfirmDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ConfirmDialog dialog = new ConfirmDialog(mContext, R.style.BaseDialog);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setWindowAnimations(R.style.DialogAnim);
            }

            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.confirm_dialog_layout, null);
            // dialog.setContentView(layout);

            tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
            if (TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            }

            ImageButton ibClose = (ImageButton) layout.findViewById(R.id.ibClose);
            ibClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            LinearLayout llPanel = (LinearLayout) layout.findViewById(R.id.llPanel);
            if (contentPanelHeight != 0) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, contentPanelHeight);
                llPanel.setLayoutParams(lp);
            }

            if (customView != null) {
                llPanel.addView(customView);
            } else {
                TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
                if (!TextUtils.isEmpty(message)) {
                    tvMessage.setText(message);
                    if (messageColor != -1) {
                        tvMessage.setTextColor(messageColor);
                    }

                    if (messageSize != -1) {
                        tvMessage.setTextSize(messageSize);
                    }

                    if (paddingTop != 0 && paddingBottom != 0) {
                        tvMessage.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                    }

                    if (isBold) {
                        TextPaint tp = tvMessage.getPaint();
                        tp.setFakeBoldText(true);
                    }

                    tvMessage.setCompoundDrawablesWithIntrinsicBounds(msgDrawableLeft, msgDrawableTop,
                            msgDrawableRight, msgDrawableBottom);
                }
            }

            TextView tvNeutral = (TextView) layout.findViewById(R.id.tvNeutral);
            if (!TextUtils.isEmpty(mNeutralButtonText)) {
                tvNeutral.setText(mNeutralButtonText);
            }
            tvNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNeutralButtonListener != null) {
                        mNeutralButtonListener.onClick(dialog, BUTTON_NEUTRAL);
                    }
                    if (autoDismiss) {
                        dialog.dismiss();
                    }
                }
            });

            if (!TextUtils.isEmpty(mPositiveButtonText) && !TextUtils.isEmpty(mNegativeButtonText)) {
                tvNeutral.setVisibility(View.GONE);
                TextView tvPositive = (TextView) layout.findViewById(R.id.tvPositive);
                tvPositive.setText(mPositiveButtonText);
                tvPositive.setVisibility(View.VISIBLE);
                tvPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mPositiveButtonListener != null) {
                            mPositiveButtonListener.onClick(dialog,
                                    BUTTON_POSITIVE);
                        }
                        if (autoDismiss) {
                            dialog.dismiss();
                        }
                    }
                });

                TextView tvNegative = (TextView) layout.findViewById(R.id.tvNegative);
                tvNegative.setText(mNegativeButtonText);
                tvNegative.setVisibility(View.VISIBLE);
                tvNegative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mNegativeButtonListener != null) {
                            mNegativeButtonListener.onClick(dialog,
                                    BUTTON_NEGATIVE);
                        }
                        dialog.dismiss();
                    }
                });
            }


            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            Window dialogWindow = dialog.getWindow();
            DisplayMetrics dm = new DisplayMetrics();
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (dm.widthPixels * mWidthRate);
            lp.height = LayoutParams.WRAP_CONTENT;
            lp.dimAmount = dimAmount;
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);

            return dialog;
        }
    }
}
