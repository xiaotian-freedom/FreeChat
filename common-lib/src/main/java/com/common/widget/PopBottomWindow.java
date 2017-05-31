package com.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.common.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PopBottomWindow extends PopupWindow {

    public PopBottomWindow(Context context) {
        super(context);
    }

    public static class Builder {

        private Context mContext;
        private List<String> mStrItemList = new ArrayList<>();
        private List<Integer> mImgItemList = new ArrayList<>();

        private OnWhichClickListener mOnWhichClickListener;

        private int textColor;
        private boolean isShowHtml = false;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setItemTextColor(int color) {
            textColor = color;
            return this;
        }

        public Builder showHtml(boolean isShowHtml) {
            this.isShowHtml = isShowHtml;
            return this;
        }

        public Builder setItems(int itemsId, final OnWhichClickListener listener) {
            CharSequence[] itemArray = mContext.getResources().getTextArray(itemsId);
            setItems(itemArray, listener);
            return this;
        }

        public Builder setItems(CharSequence[] itemArray, final OnWhichClickListener listener) {
            for (CharSequence cs : itemArray) {
                mStrItemList.add(cs.toString());
            }
            mOnWhichClickListener = listener;
            return this;
        }

        public Builder setItems(List<String> itemList, final OnWhichClickListener listener) {
            mStrItemList = itemList;
            mOnWhichClickListener = listener;
            return this;
        }

        public Builder setItems(Integer[] itemArray, final OnWhichClickListener listener) {
            mImgItemList = Arrays.asList(itemArray);
            mOnWhichClickListener = listener;
            return this;
        }

        public Builder setItems(ArrayList<Integer> itemList, final OnWhichClickListener listener) {
            mImgItemList = itemList;
            mOnWhichClickListener = listener;
            return this;
        }

        public interface OnWhichClickListener {
            void onWhichClick(View v, int position);
        }

        @SuppressLint("InflateParams")
        public PopBottomWindow create() {
            final PopBottomWindow popWindow = new PopBottomWindow(mContext);

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.pop_bottom_window, null);
            final LinearLayout linearContent = (LinearLayout) layout.findViewById(R.id.linear_content);
            LinearLayout linearItems = (LinearLayout) layout.findViewById(R.id.linear_items);
            if (mStrItemList != null && !mStrItemList.isEmpty()) {
                int size = mStrItemList.size();
                for (int i = 0; i < size; i++) {
                    TextView textView = initTextView(linearItems, inflater, i, size);
                    String text = mStrItemList.get(i);
                    if (isShowHtml) {
                        textView.setText(Html.fromHtml(text));
                    } else {
                        textView.setText(text);
                    }

                    if (textColor != 0) {
                        textView.setTextColor(textColor);
                    }
                    textView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mOnWhichClickListener != null) {
                                mOnWhichClickListener.onWhichClick(v, v.getId());
                            }
                            popWindow.dismiss();
                        }
                    });
                    linearItems.addView(textView);
                }
            } else if (mImgItemList != null && !mImgItemList.isEmpty()) {
                int size = mImgItemList.size();
                for (int i = 0; i < size; i++) {
                    ImageView imageView = initImageView(linearItems, inflater, i, size);
                    int resId = mImgItemList.get(i);
                    imageView.setImageResource(resId);
                    imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mOnWhichClickListener != null) {
                                mOnWhichClickListener.onWhichClick(v, v.getId());
                            }
                            popWindow.dismiss();
                        }
                    });
                    linearItems.addView(imageView);
                }
            }

            popWindow.setContentView(layout);
            popWindow.setWidth(LayoutParams.MATCH_PARENT);
            popWindow.setHeight(LayoutParams.WRAP_CONTENT);
            popWindow.setFocusable(true);
            popWindow.setTouchable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().
                    getColor(R.color.translucent_background)));

            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
            linearContent.startAnimation(anim);

            // 点击取消
            TextView tvCancel = (TextView) layout.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
                    linearContent.startAnimation(anim);
                    linearContent.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            popWindow.dismiss();
                        }
                    }, 300);
                }
            });

            // 点击Layout布局外
            layout.setOnTouchListener(new OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int height = v.findViewById(R.id.linear_content).getTop();
                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
                            linearContent.startAnimation(anim);
                            linearContent.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    popWindow.dismiss();
                                }
                            }, 300);
                        }
                    }

                    return true;
                }
            });

            return popWindow;
        }

        private TextView initTextView(LinearLayout linearItems, LayoutInflater inflater, int i, int size) {
            if (i > 0) {
                View lineView = inflater.inflate(R.layout.pop_bottom_window_line_view, linearItems,
                        false);
                linearItems.addView(lineView);
            }
            TextView textView = (TextView) inflater.inflate(
                    R.layout.pop_bottom_window_text_view, linearItems, false);
            if (size == 1) {
                textView.setBackgroundResource(R.drawable.holder_item_round_selector);
            } else if (size >= 2) {
                if (i == 0) {
                    textView.setBackgroundResource(R.drawable.holder_item_round_top_selector);
                } else if (i == size - 1) {
                    textView.setBackgroundResource(R.drawable.holder_item_round_bottom_selector);
                } else {
                    textView.setBackgroundResource(R.drawable.holder_item_shape_selector);
                }
            }
            textView.setId(i);
            return textView;
        }

        private ImageView initImageView(LinearLayout linearItems, LayoutInflater inflater, int i, int size) {
            if (i > 0) {
                View lineView = inflater.inflate(R.layout.pop_bottom_window_line_view, linearItems,
                        false);
                linearItems.addView(lineView);
            }
            ImageView imageView = (ImageView) inflater.inflate(
                    R.layout.pop_bottom_window_image_view, linearItems, false);
            if (size == 1) {
                imageView.setBackgroundResource(R.drawable.holder_item_round_selector);
            } else if (size >= 2) {
                if (i == 0) {
                    imageView.setBackgroundResource(R.drawable.holder_item_round_top_selector);
                } else if (i == size - 1) {
                    imageView.setBackgroundResource(R.drawable.holder_item_round_bottom_selector);
                } else {
                    imageView.setBackgroundResource(R.drawable.holder_item_shape_selector);
                }
            }
            imageView.setId(i);
            return imageView;
        }

    }

}
