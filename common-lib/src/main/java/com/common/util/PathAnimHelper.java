package com.common.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Path动画工具类
 *
 * @author tianshutong
 *         Created by tianshutong on 2017/1/17.
 */

public class PathAnimHelper {

    //默认外圆最后一圈动画时长
    private static final int mDefaultLastDuration = 500;

    //外圆动画时长
    private int mCircleDuration;

    //是否循环显示 默认为true
    private boolean isInfinite = true;

    //是否加载成功
    private boolean isSuccess;

    //是否加载失败
    private boolean isFail;

    //外圆path
    private Path mCircleSourcePath;

    //外圆动画path
    private Path mCircleAnimPath;

    //对号path
    private Path mSuccessSourcePath;

    //对号动画path
    private Path mSuccessAnimPath;

    //左侧叉号path
    private Path mFailLeftSourcePath;

    //左侧叉号动画path
    private Path mFailLeftAnimPath;

    //右侧叉号path
    private Path mFailRightSourcePath;

    //右侧叉号动画path
    private Path mFailRightAnimPath;

    //执行动画的view
    private View targetView;

    //外圆动画属性
    private ValueAnimator circleValueAnimator;

    //对号动画属性
    private ValueAnimator successValueAnimator;

    //左侧叉号动画属性
    private ValueAnimator failLeftValueAnimator;

    //右侧叉号动画属性
    private ValueAnimator failRightValueAnimator;

    private Animator.AnimatorListener animatorListener;

    public PathAnimHelper(View view, Path circleSourcePath, Path circleAnimPath,
                          Path successSourcePath, Path successAnimPath,
                          Path failLeftSourcePath, Path failLeftAnimPath,
                          Path failRightSourcePath, Path failRightAnimPath,
                          int duration) {
        if (view == null || circleSourcePath == null || circleAnimPath == null
                || successSourcePath == null || successAnimPath == null
                || failLeftSourcePath == null || failLeftAnimPath == null
                || failRightSourcePath == null || failRightAnimPath == null) {
            throw new RuntimeException("view和mSourcePath和mAnimPath一个都不能少");
        }
        this.isSuccess = false;
        this.isFail = false;
        this.targetView = view;
        this.mCircleSourcePath = circleSourcePath;
        this.mCircleAnimPath = circleAnimPath;
        this.mSuccessSourcePath = successSourcePath;
        this.mSuccessAnimPath = successAnimPath;
        this.mFailLeftAnimPath = failLeftAnimPath;
        this.mFailLeftSourcePath = failLeftSourcePath;
        this.mFailRightSourcePath = failRightSourcePath;
        this.mFailRightAnimPath = failRightAnimPath;
        this.mCircleDuration = duration;
    }

    /**
     * 设置外圆动画时长
     *
     * @param duration
     */
    public void setCircleDuration(int duration) {
        this.mCircleDuration = duration;
    }

    /**
     * 设置是否循环
     *
     * @param isInfinite
     */
    public void setInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }

    /**
     * 获取循环值
     *
     * @return
     */
    public boolean getInfinite() {
        return isInfinite;
    }

    /**
     * 计算path动画时长并组装
     *
     * @param view
     * @param circleSourcePath
     * @param circleAnimPath
     * @param successSourcePath
     * @param successAnimPath
     * @param failLeftSourcePath
     * @param failLeftAnimPath
     * @param failRightSourcePath
     * @param failRightAnimPath
     * @param circleDuration
     * @param isInfinite
     */
    private void startPathAnim(View view, Path circleSourcePath, Path circleAnimPath,
                               Path successSourcePath, Path successAnimPath,
                               Path failLeftSourcePath, Path failLeftAnimPath,
                               Path failRightSourcePath, Path failRightAnimPath,
                               int circleDuration, boolean isInfinite) {
        if (view == null || circleSourcePath == null || circleAnimPath == null
                || successSourcePath == null || successAnimPath == null
                || failLeftSourcePath == null || failLeftAnimPath == null
                || failRightSourcePath == null || failRightAnimPath == null) {
            return;
        }

        //外圆的PathMeasure及时长
        PathMeasure circlePathMeasure = new PathMeasure(circleSourcePath, false);

        //计算每段path的时长
        int circleCount = 0;
        while (circlePathMeasure.getLength() != 0) {
            circlePathMeasure.nextContour();
            circleCount++;
        }
        //分段后要重组源path,不写这句会不显示path
        circlePathMeasure.setPath(circleSourcePath, false);

        //对号的PathMeasure及时长
        PathMeasure successPathMeasure = new PathMeasure(successSourcePath, false);
        int successCount = 0;
        while (successPathMeasure.getLength() != 0) {
            successPathMeasure.nextContour();
            successCount++;
        }
        successPathMeasure.setPath(successSourcePath, false);

        //左侧叉号的PathMeasure及时长
        PathMeasure failLeftMeasure = new PathMeasure(failLeftSourcePath, false);
        int failLeftCount = 0;
        while (failLeftMeasure.getLength() != 0) {
            failLeftMeasure.nextContour();
            failLeftCount++;
        }
        failLeftMeasure.setPath(failLeftSourcePath, false);

        //右侧叉号的PathMeasure及时长
        PathMeasure failRightMeasure = new PathMeasure(failRightSourcePath, false);
        int failRightCount = 0;
        while (failRightMeasure.getLength() != 0) {
            failRightMeasure.nextContour();
            failRightCount++;
        }
        failRightMeasure.setPath(failRightSourcePath, false);

        loopAnim(view, circlePathMeasure, circleAnimPath,
                successPathMeasure, successAnimPath,
                failLeftMeasure, failLeftAnimPath,
                failRightMeasure, failRightAnimPath,
                circleDuration / circleCount, circleDuration / successCount,
                circleDuration / 2 / failLeftCount, circleDuration / 2 / failRightCount, isInfinite);
    }

    /**
     * view调用启动动画
     */
    public void startAnim() {
        startPathAnim(targetView, mCircleSourcePath, mCircleAnimPath,
                mSuccessSourcePath, mSuccessAnimPath,
                mFailLeftSourcePath, mFailLeftAnimPath,
                mFailRightSourcePath, mFailRightAnimPath,
                mCircleDuration, isInfinite);
    }

    /**
     * 停止循环动画
     */
    private void stopPathAnim() {
        setInfinite(false);
        setCircleDuration(mDefaultLastDuration);
    }

    /**
     * 加载成功
     */
    public void loadingSuccess() {
        isSuccess = true;
        stopPathAnim();
    }

    /**
     * 加载失败
     */
    public void loadingFail() {
        isFail = true;
        stopPathAnim();
    }


    /**
     * 停止外圆动画
     */
    private void stopCircleAnim() {
        if (circleValueAnimator != null && circleValueAnimator.isRunning()) {
            circleValueAnimator.end();
        }
    }

    /**
     * 停止对号动画
     */
    private void stopSuccessAnim() {
        if (successValueAnimator != null && successValueAnimator.isRunning()) {
            successValueAnimator.end();
        }
    }

    /**
     * 停止叉号动画
     */
    private void stopFailAnim() {
        if (failLeftValueAnimator != null && failLeftValueAnimator.isRunning()) {
            failLeftValueAnimator.end();
        }
        if (failRightValueAnimator != null && failRightValueAnimator.isRunning()) {
            failRightValueAnimator.end();
        }
    }

    /**
     * 监听动画结束
     *
     * @param listener
     */
    public void setOnAnimEndCallBack(Animator.AnimatorListener listener) {
        if (listener != null) {
            animatorListener = listener;
        }
    }

    /**
     * 重置动画
     */
    public void resetAnim(int duration) {
        isSuccess = false;
        isFail = false;
        setInfinite(true);
        mCircleAnimPath.reset();
        mCircleAnimPath.lineTo(0, 0);
        mSuccessAnimPath.reset();
        mSuccessAnimPath.lineTo(0, 0);
        mFailLeftAnimPath.reset();
        mFailLeftAnimPath.lineTo(0, 0);
        mFailRightAnimPath.reset();
        mFailRightAnimPath.lineTo(0, 0);
        setCircleDuration(duration);
        stopSuccessAnim();
        stopFailAnim();
        startAnim();
    }

    /**
     * 循环动画
     * <p>
     * 执行取出的每段path动画
     *
     * @param view
     * @param circleMeasure
     * @param circleAnimPath
     * @param circleAnimPath
     * @param successMeasure
     * @param successAnimPath
     * @param failLeftMeasure
     * @param failLeftAnimPath
     * @param failRightMeasure
     * @param failRightAnimPath
     * @param circleDuration
     * @param successDuration
     * @param failLeftDuration
     * @param failRightDuration
     * @param isInfinite
     */
    private void loopAnim(final View view, final PathMeasure circleMeasure, final Path circleAnimPath,
                          final PathMeasure successMeasure, final Path successAnimPath,
                          final PathMeasure failLeftMeasure, final Path failLeftAnimPath,
                          final PathMeasure failRightMeasure, final Path failRightAnimPath,
                          int circleDuration, final int successDuration,
                          final int failLeftDuration, final int failRightDuration,
                          final boolean isInfinite) {
        if (isSuccess || isFail) {
            stopCircleAnim();
        }
        circleValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        circleValueAnimator.setDuration(circleDuration);
        if (isInfinite) {
            circleValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        } else {
            circleValueAnimator.setRepeatCount(0);
        }
        circleValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        circleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (getInfinite()) {
                    onPathAnimCallBack(circleMeasure, circleAnimPath, animation);
                } else {
                    onOverAnimCallBack(circleMeasure, circleAnimPath, animation);
                    if (isSuccess) {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loopSuccessAnim(view, successMeasure, successAnimPath, successDuration);
                            }
                        }, 100);
                    }
                    if (isFail) {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loopFailLeftAnim(view, failLeftMeasure, failLeftAnimPath,
                                        failRightMeasure, failRightAnimPath,
                                        failLeftDuration, failRightDuration);
                            }
                        }, 100);
                    }
                }
                view.postInvalidate();
            }
        });
        circleValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                circleMeasure.getSegment(0, circleMeasure.getLength(), circleAnimPath, true);
            }
        });
        circleValueAnimator.start();
    }

    /**
     * 加载成功动画
     *
     * @param view
     * @param successMeasure
     * @param successAnimPath
     * @param duration
     */
    private void loopSuccessAnim(final View view, final PathMeasure successMeasure,
                                 final Path successAnimPath, int duration) {
        successValueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        successValueAnimator.setDuration(duration);
        successValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        successValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                onOverAnimCallBack(successMeasure, successAnimPath, animation);
                view.postInvalidate();
            }
        });
        if (animatorListener != null) {
            successValueAnimator.addListener(animatorListener);
        }
        successValueAnimator.start();
    }

    /**
     * 加载左侧叉号动画
     *
     * @param view
     * @param failLeftMeasure
     * @param failLeftAnimPath
     * @param failRightMeasure
     * @param failRightAnimPath
     * @param leftDuration
     * @param rightDuration
     */
    private void loopFailLeftAnim(final View view, final PathMeasure failLeftMeasure, final Path failLeftAnimPath,
                                  final PathMeasure failRightMeasure, final Path failRightAnimPath,
                                  final int leftDuration, final int rightDuration) {
        failLeftValueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        failLeftValueAnimator.setDuration(leftDuration);
        failLeftValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        failLeftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                onOverAnimCallBack(failLeftMeasure, failLeftAnimPath, animation);
                view.postInvalidate();
            }
        });
        failLeftValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                loopFailRightAnim(view, failRightMeasure, failRightAnimPath, rightDuration);
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
        failLeftValueAnimator.start();
    }

    /**
     * 加载右侧叉号动画
     *
     * @param view
     * @param failMeasure
     * @param failAnimPath
     * @param duration
     */
    private void loopFailRightAnim(final View view, final PathMeasure failMeasure,
                                   final Path failAnimPath, int duration) {
        failRightValueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        failRightValueAnimator.setDuration(duration);
        failRightValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        failRightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                onOverAnimCallBack(failMeasure, failAnimPath, animation);
                view.postInvalidate();
            }
        });
        if (animatorListener != null) {
            failRightValueAnimator.addListener(animatorListener);
        }
        failRightValueAnimator.start();
    }

    /**
     * 取出每段path回调
     * 不断变换起始位置
     *
     * @param pathMeasure
     * @param mAnimPath
     * @param animator
     */
    private void onPathAnimCallBack(PathMeasure pathMeasure, Path mAnimPath, ValueAnimator animator) {
        mAnimPath.reset();
        //解决硬件加速bug
        mAnimPath.lineTo(0, 0);
        float value = (float) animator.getAnimatedValue();
        float stop = pathMeasure.getLength() * value;
        float start = (float) (stop - ((0.5 - Math.abs(value - 0.5))
                * 200f));
        pathMeasure.getSegment(start, stop, mAnimPath, true);
    }

    /**
     * 内部path回调
     *
     * @param pathMeasure
     * @param mAnimPath
     * @param animator
     */
    private void onOverAnimCallBack(PathMeasure pathMeasure, Path mAnimPath, ValueAnimator animator) {
        float value = (float) animator.getAnimatedValue();
        pathMeasure.getSegment(0, pathMeasure.getLength() * value, mAnimPath, true);
    }

}
