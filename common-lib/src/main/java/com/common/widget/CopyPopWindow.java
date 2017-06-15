package com.common.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.common.R;
import com.common.util.DensityUtil;

/**
 * Created by tianshutong on 2017/6/14.
 */

public class CopyPopWindow extends PopupWindow {

    public CopyPopWindow(Context context) {
        super(context);
    }

    public static class Builder {

        private Context mContext;
        private int mLocationX = -1;
        private int mType = -1;
        private OnWhichClickListener mOnClickListener;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setLocationX(int locationX) {
            this.mLocationX = locationX;
            return this;
        }

        public Builder setType(int type) {
            this.mType = type;
            return this;
        }

        public Builder setOnWhichClickListener(OnWhichClickListener listener) {
            this.mOnClickListener = listener;
            return this;
        }

        public interface OnWhichClickListener {
            void onItemClick(View v, int position);
        }

        public CopyPopWindow create() {
            final CopyPopWindow copyPopWindow = new CopyPopWindow(mContext);

            final View layout = LayoutInflater.from(mContext).inflate(R.layout.pop_chat_copy_window, null);
            TextView copyView = (TextView) layout.findViewById(R.id.copy);
            copyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onItemClick(v, 0);
                        copyPopWindow.dismiss();
                    }
                }
            });
            TextView shareView = (TextView) layout.findViewById(R.id.share);
            shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onItemClick(v, 1);
                        copyPopWindow.dismiss();
                    }
                }
            });
            TextView deleteView = (TextView) layout.findViewById(R.id.delete);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onItemClick(v, 2);
                        copyPopWindow.dismiss();
                    }
                }
            });
            ImageView arrow = (ImageView) layout.findViewById(R.id.arrow);
            if (mLocationX != -1) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) arrow.getLayoutParams();
                int width = 0;

                if (mType != -1) {
                    if (mType == 0) {
                        width = -DensityUtil.dip2px(mContext, 20);
                    } else if (mType == 1) {
                        width = DensityUtil.dip2px(mContext, 20);
                    }
                }
                lp.leftMargin = mLocationX + width;
            }
            copyPopWindow.setContentView(layout);
            copyPopWindow.setOutsideTouchable(true);
            copyPopWindow.setFocusable(true);
            copyPopWindow.setTouchable(true);
            copyPopWindow.setBackgroundDrawable(new ColorDrawable());
            copyPopWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            copyPopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

            return copyPopWindow;
        }
    }
}
