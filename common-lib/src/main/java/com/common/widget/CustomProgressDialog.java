package com.common.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common.R;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;

public class CustomProgressDialog extends Dialog {

    private static final float WIDTH_RATE = 0.87F;

    private static boolean mHasStarted;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    /*
     * Use a separate handler to update the text views as they must be updated
     * on the same thread that created them.
     */
    private static class MsgHandler extends Handler {

        private CustomProgressDialog.Builder theBuilder;

        public MsgHandler(CustomProgressDialog.Builder builder) {
            super();
            WeakReference<Builder> wrBuilder = new WeakReference<>(builder);
            theBuilder = wrBuilder.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

			/* Update the number and percent */
            int progress = theBuilder.mProgress.getProgress();
            int max = theBuilder.mProgress.getMax();
            if (theBuilder.mProgressNumberFormat != null) {
                String format = theBuilder.mProgressNumberFormat;
                theBuilder.mProgressNumber.setText(String.format(format, progress, max));
            } else {
                theBuilder.mProgressNumber.setText("");
            }
            if (theBuilder.mProgressPercentFormat != null) {
                double percent = (double) progress / (double) max;
                SpannableString tmp = new SpannableString(
                        theBuilder.mProgressPercentFormat.format(percent));
                tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                        tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                theBuilder.mProgressPercent.setText(tmp);
            } else {
                theBuilder.mProgressPercent.setText("");
            }
        }
    }

    /**
     * Helper class for creating a custom dialog
     **/
    public static class Builder {
        private Context mContext;
        private ProgressBar mProgress;

        private TextView mProgressNumber;
        private String mProgressNumberFormat;
        private TextView mProgressPercent;
        private NumberFormat mProgressPercentFormat;

        private int mMax;
        private int mProgressVal;
        private int mSecondaryProgressVal;
        private int mIncrementBy;
        private int mIncrementSecondaryBy;
        private Drawable mProgressDrawable;
        private Drawable mIndeterminateDrawable;
        private boolean mIndeterminate;

        private MsgHandler mViewUpdateHandler;

        private TextView tvTitle;

        private String title;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private String mNeutralButtonText;

        private OnClickListener mPositiveButtonListener;
        private OnClickListener mNegativeButtonListener;
        private OnClickListener mNeutralButtonListener;

        public Builder(Context context) {
            this.mContext = context;
            initFormats();
            mViewUpdateHandler = new MsgHandler(this);
        }

        private void initFormats() {
            mProgressNumberFormat = "%1d/%2d";
            mProgressPercentFormat = NumberFormat.getPercentInstance();
            mProgressPercentFormat.setMaximumFractionDigits(0);
        }

        public void setProgress(int value) {
            if (mHasStarted) {
                mProgress.setProgress(value);
                onProgressChanged();
            } else {
                mProgressVal = value;
            }
        }

        public void setSecondaryProgress(int secondaryProgress) {
            if (mProgress != null) {
                mProgress.setSecondaryProgress(secondaryProgress);
                onProgressChanged();
            } else {
                mSecondaryProgressVal = secondaryProgress;
            }
        }

        public int getProgress() {
            if (mProgress != null) {
                return mProgress.getProgress();
            }
            return mProgressVal;
        }

        public int getSecondaryProgress() {
            if (mProgress != null) {
                return mProgress.getSecondaryProgress();
            }
            return mSecondaryProgressVal;
        }

        public int getMax() {
            if (mProgress != null) {
                return mProgress.getMax();
            }
            return mMax;
        }

        public Builder setMax(int max) {
            if (mProgress != null) {
                mProgress.setMax(max);
                onProgressChanged();
            } else {
                mMax = max;
            }
            return this;
        }

        public Builder incrementProgressBy(int diff) {
            if (mProgress != null) {
                mProgress.incrementProgressBy(diff);
                onProgressChanged();
            } else {
                mIncrementBy += diff;
            }
            return this;
        }

        public Builder incrementSecondaryProgressBy(int diff) {
            if (mProgress != null) {
                mProgress.incrementSecondaryProgressBy(diff);
                onProgressChanged();
            } else {
                mIncrementSecondaryBy += diff;
            }
            return this;
        }

        public Builder setProgressDrawable(Drawable d) {
            if (mProgress != null) {
                mProgress.setProgressDrawable(d);
            } else {
                mProgressDrawable = d;
            }
            return this;
        }

        public Builder setIndeterminateDrawable(Drawable d) {
            if (mProgress != null) {
                mProgress.setIndeterminateDrawable(d);
            } else {
                mIndeterminateDrawable = d;
            }
            return this;
        }

        public Builder setIndeterminate(boolean indeterminate) {
            if (mProgress != null) {
                mProgress.setIndeterminate(indeterminate);
            } else {
                mIndeterminate = indeterminate;
            }
            return this;
        }

        public boolean isIndeterminate() {
            if (mProgress != null) {
                return mProgress.isIndeterminate();
            }
            return mIndeterminate;
        }

        /**
         * Change the format of the small text showing current and maximum units
         * of progress. The default is "%1d/%2d". Should not be called during
         * the number is progressing.
         *
         * @param format A string passed to {@link String#format String.format()};
         *               use "%1d" for the current number and "%2d" for the
         *               maximum. If null, nothing will be shown.
         */
        public Builder setProgressNumberFormat(String format) {
            mProgressNumberFormat = format;
            onProgressChanged();
            return this;
        }

        /**
         * Change the format of the small text showing the percentage of
         * progress. The default is {@link NumberFormat#getPercentInstance()
         * NumberFormat.getPercentageInstnace().} Should not be called during
         * the number is progressing.
         *
         * @param format An instance of a {@link NumberFormat} to generate the
         *               percentage text. If null, nothing will be shown.
         */
        public Builder setProgressPercentFormat(NumberFormat format) {
            mProgressPercentFormat = format;
            onProgressChanged();
            return this;
        }

        private void onProgressChanged() {
            if (mViewUpdateHandler != null
                    && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }

        // /////////////////////////////////////////////////////////////////////////////////////////

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

        @SuppressLint("InflateParams")
        public CustomProgressDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final CustomProgressDialog dialog = new CustomProgressDialog(
                    mContext, R.style.BaseDialog);

            View layout = inflater.inflate(R.layout.alert_dialog_progress, null);
            mProgress = (ProgressBar) layout.findViewById(R.id.progress);
            mProgressNumber = (TextView) layout.findViewById(R.id.progress_number);
            mProgressPercent = (TextView) layout.findViewById(R.id.progress_percent);

            // ////////////////////////////////////////////////////////////////
            if (mMax > 0) {
                setMax(mMax);
            }
            if (mProgressVal > 0) {
                setProgress(mProgressVal);
            }
            if (mSecondaryProgressVal > 0) {
                setSecondaryProgress(mSecondaryProgressVal);
            }
            if (mIncrementBy > 0) {
                incrementProgressBy(mIncrementBy);
            }
            if (mIncrementSecondaryBy > 0) {
                incrementSecondaryProgressBy(mIncrementSecondaryBy);
            }
            if (mProgressDrawable != null) {
                setProgressDrawable(mProgressDrawable);
            }
            if (mIndeterminateDrawable != null) {
                setIndeterminateDrawable(mIndeterminateDrawable);
            }
            setIndeterminate(mIndeterminate);
            onProgressChanged();
            // /////////////////////////////////////////////////////////////////

            tvTitle = (TextView) layout.findViewById(R.id.tv_title);
            tvTitle.setText(title);

            TextView tvNeutral = (TextView) layout.findViewById(R.id.tv_neutral);
            if (!TextUtils.isEmpty(mNeutralButtonText)) {
                tvNeutral.setText(mNeutralButtonText);
            }
            tvNeutral.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mNeutralButtonListener != null) {
                        mNeutralButtonListener.onClick(dialog, BUTTON_NEUTRAL);
                    }
                    dialog.dismiss();
                }
            });

            if (!TextUtils.isEmpty(mPositiveButtonText)
                    && !TextUtils.isEmpty(mNegativeButtonText)) {
                tvNeutral.setVisibility(View.GONE);
                TextView tvPositive = (TextView) layout.findViewById(R.id.tv_positive);
                tvPositive.setText(mPositiveButtonText);
                tvPositive.setVisibility(View.VISIBLE);
                tvPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (mPositiveButtonListener != null) {
                            mPositiveButtonListener.onClick(dialog,
                                    BUTTON_POSITIVE);
                        }
                        dialog.dismiss();
                    }
                });

                View divider = layout.findViewById(R.id.buttons_divider);
                divider.setVisibility(View.VISIBLE);

                TextView tvNegative = (TextView) layout.findViewById(R.id.tv_negative);
                tvNegative.setText(mNegativeButtonText);
                tvNegative.setVisibility(View.VISIBLE);
                tvNegative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (mNegativeButtonListener != null) {
                            mNegativeButtonListener.onClick(dialog,
                                    BUTTON_NEGATIVE);
                        }
                        dialog.dismiss();
                    }
                });
            }

            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics dm = new DisplayMetrics();
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
            lp.width = (int) (dm.widthPixels * WIDTH_RATE);
            lp.height = LayoutParams.WRAP_CONTENT;
            lp.dimAmount = 0.6f;
            dialogWindow.setAttributes(lp);

            return dialog;
        }
    }

}
