package com.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.common.R;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

/**
 * 标签筛选
 * Created by tianshutong on 16/7/26.
 */

public class FilterDialog extends Dialog {

    public FilterDialog(Context context) {
        super(context);
    }

    public FilterDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FilterDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {

        private Context mContext;
        private float mWidthRate = 0.87F;
        private float dimAmount = 0.6f;

        private TagAdapter<String> mTagAdapter;
        private TagFlowLayout.OnTagClickListener mTagClickListener;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public FilterDialog.Builder setWidthRate(float rate) {
            mWidthRate = rate;
            return this;
        }

        public FilterDialog.Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        public FilterDialog.Builder setTagAdapter(TagAdapter<String> mTagAdapter) {
            this.mTagAdapter = mTagAdapter;
            return this;
        }

        public FilterDialog.Builder setOnTagClickListener(TagFlowLayout.OnTagClickListener tagClickListener) {
            mTagClickListener = tagClickListener;
            return this;
        }

        public FilterDialog create() {
            final FilterDialog dialog = new FilterDialog(mContext, R.style.BaseDialog);
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.filter_dialog_layout, null);
            ImageView ivClose = (ImageView) layout.findViewById(R.id.filter_iv_close);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            TagFlowLayout flowLayout = (TagFlowLayout) layout.findViewById(R.id.filter_flow_dialog);
            if (mTagAdapter != null) {
                flowLayout.setAdapter(mTagAdapter);
            }
            if (mTagClickListener != null) {
                flowLayout.setOnTagClickListener(mTagClickListener);
            }

            dialog.addContentView(layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window dialogWindow = dialog.getWindow();
            DisplayMetrics dm = new DisplayMetrics();
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (dm.widthPixels * mWidthRate);
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.dimAmount = dimAmount;
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
            return dialog;
        }

    }


}
