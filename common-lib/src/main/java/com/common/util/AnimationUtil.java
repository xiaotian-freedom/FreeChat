package com.common.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.common.common.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.common.common.Constants.ANIM_500;

/**
 * 平移动画工具类
 * Created by tianshutong on 2016/12/8.
 */

public class AnimationUtil {

    /**
     * 向下平移
     *
     * @param view
     * @param height
     */
    public static void SlideDown(View view, int height) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, Constants.TRANS_PROPERTY, 0, height);
        objectAnimator.setDuration(Constants.ANIM_300);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    /**
     * 向上平移
     * @param view
     * @param height
     */
    public static void SlideUp(View view, int height) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, Constants.TRANS_PROPERTY, height, 0);
        objectAnimator.setDuration(Constants.ANIM_300);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    /**
     * 列表进入动画
     *
     * @param view
     * @param mList
     */
    public static void runEnterAnimation(View view, int width, List<?> mList) {
        if (mList.size() == 0) {
            return;
        }
        ObjectAnimator translatorAnim = ObjectAnimator.ofFloat(view, Constants.TRANS_PROPERTY_X, width, 0);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, 0.f, 1.f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(Constants.ANIM_300);
        animatorSet.setTarget(view);
        animatorSet.playTogether(translatorAnim, alphaAnim);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    /**
     * view 渐变动画
     *
     * @param view
     */
    public static void startAlphaAnim(final View view) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, 0.f, 1.f);
        alphaAnim.setDuration(Constants.ANIM_300);
        alphaAnim.setInterpolator(new DecelerateInterpolator());
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnim.start();
    }

    /**
     * view 渐变动画
     *
     * @param view
     */
    public static void startAlphaAnim(final View view, float endAlpha) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, 0.f, endAlpha);
        alphaAnim.setDuration(Constants.ANIM_300);
        alphaAnim.setInterpolator(new DecelerateInterpolator());
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnim.start();
    }

    /**
     * 隐藏view
     * @param view
     */
    public static void endAlphaAnim(final View view) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, 1.f, 0.f);
        alphaAnim.setDuration(Constants.ANIM_300);
        alphaAnim.setInterpolator(new DecelerateInterpolator());
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnim.start();
    }

    /**
     * 隐藏view
     * @param view
     */
    public static void endAlphaAnim(final View view, float startAlpha) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, startAlpha, 0.f);
        alphaAnim.setDuration(Constants.ANIM_300);
        alphaAnim.setInterpolator(new DecelerateInterpolator());
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnim.start();
    }

    public static void startAlphaAnim(final int duration, final View view) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, 0.f, 1.f);
        alphaAnim.setDuration(duration);
        alphaAnim.setInterpolator(new DecelerateInterpolator());
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnim.start();
    }

    /**
     * 渐变动画
     * 一个view显示
     * 多个view消失
     *
     * @param visibleView
     * @param params
     */
    public static void startAlphaAnim(int duration, final View visibleView, final View... params) {
        ObjectAnimator objectAnimatorVisible =
                ObjectAnimator.ofFloat(visibleView, Constants.ALPHA_PROPERTY, 0.0f, 1.0f);
        final List<ObjectAnimator> objectAnimatorList = new ArrayList<>();
        for (View v : params) {
            ObjectAnimator objectAnimatorGone =
                    ObjectAnimator.ofFloat(v, Constants.ALPHA_PROPERTY, 1.0f, 0.0f);
            objectAnimatorList.add(objectAnimatorGone);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.playTogether(objectAnimatorVisible, objectAnimatorList.iterator().next());
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                visibleView.setVisibility(View.VISIBLE);
                for (View v : params) {
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    /**
     * 缩放动画
     *
     * @param view
     */
    public static void startScaleAnim(View view) {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_X_PROPERTY, 1.f, .8f, 1.f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_Y_PROPERTY, 1.f, .8f, 1.f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(Constants.ANIM_300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(scaleXAnim, scaleYAnim);
        animatorSet.start();
    }

    /**
     * 扩展缩放动画
     *
     * @param view
     * @param listener
     */
    public static void expandScaleAnim(View view, Animator.AnimatorListener listener) {
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_Y_PROPERTY, 0.f, 1.f);
        scaleYAnim.setDuration(Constants.ANIM_300);
        scaleYAnim.setInterpolator(new DecelerateInterpolator());
        if (listener != null) {
            scaleYAnim.addListener(listener);
        }
        scaleYAnim.start();
    }

    /**
     * 顺时针旋转
     *
     * @param view
     */
    public static void rotationAnim(View view) {
        view.clearAnimation();
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, Constants.ROTATION, 0, 225);
        rotation.setDuration(ANIM_500);
        rotation.setInterpolator(new DecelerateInterpolator());
        rotation.start();
    }

    /**
     * 逆时针旋转
     *
     * @param view
     */
    public static void reverseRotation(View view) {
        view.clearAnimation();
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, Constants.ROTATION, 225, 0);
        rotation.setDuration(ANIM_500);
        rotation.setInterpolator(new DecelerateInterpolator());
        rotation.start();
    }

    /**
     * 收缩动画
     *
     * @param view
     * @param listener
     */
    public static void collapse(final View view, Animation.AnimationListener listener) {
        final int originalHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1.f) {
//                    view.setVisibility(View.GONE);//会删除两个item
                    view.getLayoutParams().height = originalHeight;
                } else {
                    view.getLayoutParams().height = originalHeight - (int) (originalHeight * interpolatedTime);
                }
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        animation.setDuration(Constants.ANIM_300);
        view.startAnimation(animation);
    }

    /**
     * 收缩动画
     *
     * @param view
     * @param originalHeight
     * @param listener
     */
    public static void collapse(final View view, final int originalHeight, Animation.AnimationListener listener) {
        view.clearAnimation();
        Animation animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1.f) {
                    view.getLayoutParams().height = originalHeight;
                } else {
                    view.getLayoutParams().height = originalHeight - (int) (originalHeight * interpolatedTime);
                }
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        animation.setDuration(Constants.ANIM_300);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }

    /**
     * 扩展动画
     *
     * @param view
     */
    public static void expand(final View view, final int originalHeight, Animation.AnimationListener listener) {
        view.clearAnimation();
        Animation animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1.f) {
                    view.getLayoutParams().height = originalHeight;
                } else {
                    view.getLayoutParams().height = (int) (originalHeight * interpolatedTime);
                }
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        animation.setDuration(Constants.ANIM_300);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }

    /**
     * 实现收缩和扩展
     *
     * @param target
     * @param startHeight
     * @param endHeight
     */
    public static void expandOrCollapse(final View target, final int startHeight, final int endHeight, Animator.AnimatorListener listener) {

        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        animator.setDuration(2 * Constants.ANIM_1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) target.getLayoutParams();
                lp.height = value;
                target.setLayoutParams(lp);

                target.requestLayout();
                target.invalidate();

                Log.i("高度值->", String.valueOf(value));
            }
        });
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
    }

    /**
     * 3D旋转
     *
     * @param view
     */
    public static void rotationY(View view) {
        RotationYAnim anim = new RotationYAnim();
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

    /**
     * 绕Y轴旋转
     */
    private static class RotationYAnim extends Animation {
        int centerX, centerY;
        Camera camera = new Camera();

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            centerX = width / 2;
            centerY = width / 2;
            setDuration(3 * Constants.ANIM_1000);
            setInterpolator(new LinearInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            final Matrix matrix = t.getMatrix();
            camera.save();
            camera.rotateY(360 * interpolatedTime);
            camera.getMatrix(matrix);
            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
            camera.restore();
        }
    }
}
