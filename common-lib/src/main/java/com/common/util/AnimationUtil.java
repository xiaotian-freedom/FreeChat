package com.common.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.common.common.Constants;

import java.util.List;

import static com.common.common.Constants.ANIM_300;

/**
 * 平移动画工具类
 * Created by tianshutong on 2016/12/8.
 */

public class AnimationUtil {

    /**
     * view 平移
     *
     * @param view
     * @param height
     */
    public static void SlideDown(View view, int height) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, Constants.TRANS_PROPERTY, view.getTranslationY(), height);
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    /**
     * 列表进入动画
     *
     * @param view
     * @param mList
     */
    public static void runEnterAnimation(View view, int height, List<?> mList) {
        if (mList.size() == 0) {
            return;
        }
        ObjectAnimator translatorAnim = ObjectAnimator.ofFloat(view, Constants.TRANS_PROPERTY, height, 0);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, Constants.ALPHA_PROPERTY, 0.f, 1.f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(Constants.ANIM_500);
        animatorSet.setTarget(view);
        animatorSet.playTogether(translatorAnim, alphaAnim);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    /**
     * 渐变动画
     * 一个view显示
     * 一个view消失
     *
     * @param visibleView
     * @param goneView
     */
    public static void startAlphaAnim(final View visibleView, final View goneView) {
        ObjectAnimator objectAnimatorVisible =
                ObjectAnimator.ofFloat(visibleView, Constants.ALPHA_PROPERTY, 0.0f, 1.0f);
        ObjectAnimator objectAnimatorGone =
                ObjectAnimator.ofFloat(goneView, Constants.ALPHA_PROPERTY, 1.0f, 0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIM_300);
        animatorSet.playTogether(objectAnimatorVisible, objectAnimatorGone);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                visibleView.setVisibility(View.VISIBLE);
                goneView.setVisibility(View.GONE);
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
     * @param view
     */
    public static void startScaleAnim(View view) {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_X_PROPERTY, 1.f, .5f, 1.f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_Y_PROPERTY, 1.f, .5f, 1.f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(Constants.ANIM_500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(scaleXAnim, scaleYAnim);
        animatorSet.start();
    }

    /**
     * 顺时针旋转
     * @param view
     */
    public static void rotationAnim(View view) {
        view.clearAnimation();
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, Constants.ROTATION, 0, 135);
        rotation.setDuration(Constants.ANIM_300);
        rotation.setInterpolator(new DecelerateInterpolator());
        rotation.start();
    }

    /**
     * 逆时针旋转
     * @param view
     */
    public static void reverseRotation(View view) {
        view.clearAnimation();
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, Constants.ROTATION, 135, 0);
        rotation.setDuration(Constants.ANIM_300);
        rotation.setInterpolator(new DecelerateInterpolator());
        rotation.start();
    }
}
