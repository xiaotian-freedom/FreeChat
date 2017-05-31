package com.common.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.R;

public class GlideHelper {

    /**
     * 展示imgview为圆形图片
     *
     * @param context   Context
     * @param url       String
     * @param imageView ImageView
     */
    public static void showRoundView(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .crossFade()
                    .centerCrop()
                    .transform(new GlideCircleTransform(context))
                    .placeholder(R.color.color_8)
                    .error(R.color.color_8)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示circleImageView类型的图片
     *
     * @param context   Context
     * @param url       String
     * @param imageView ImageView
     */
    public static void showHeadViewWithNoAnim(Context context, String url, ImageView imageView) {
        try {

            Glide.with(context)
                    .load(url)
                    .dontAnimate()
                    .centerCrop()
                    .placeholder(R.color.color_8)
                    .error(R.color.color_8)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示缩略图
     *
     * @param context   Context
     * @param url       String
     * @param imageView ImageView
     */
    public static void showSqureView(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .crossFade()
                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_8)
                    .error(R.color.color_8)
//                    .thumbnail(0.1f)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示无动画的方形图片
     *
     * @param context   Context
     * @param url       String
     * @param imageView ImageView
     */
    public static void showSqureViewDontAnim(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .dontAnimate()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_8)
                    .error(R.color.color_8)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示大图
     *
     * @param context   Context
     * @param url       String
     * @param imageView ImageView
     */
    public static void showSqureViewFitCenter(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .crossFade()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_8)
                    .error(R.color.color_8)
                    .thumbnail(0.1f)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示大图
     *
     * @param context   Context
     * @param url       String
     * @param imageView ImageView
     */
    public static void showSqureViewNoCenter(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_8)
                    .error(R.color.color_8)
//                    .thumbnail(0.1f)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示图片
     *
     * @param context          Context
     * @param url              String
     * @param imageView        ImageView
     * @param placeHolderResId int
     */
    public static void showSqureViewNoCenter(Context context, String url, ImageView imageView,
                                             int placeHolderResId) {
        try {
            Glide.with(context)
                    .load(url)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(placeHolderResId)
                    .error(placeHolderResId)
                    .thumbnail(0.1f)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载gif图片
     *
     * @param context   Context
     * @param resId     int
     * @param imageView ImageView
     */
    public static void showGif(Context context, int resId, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(resId)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
