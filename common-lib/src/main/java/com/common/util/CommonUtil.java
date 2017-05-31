package com.common.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.common.R;
import com.common.common.Constants;
import com.common.widget.ConfirmDialog;

import java.io.File;
import java.io.InputStream;

public class CommonUtil {

    public CommonUtil() {
        // TODO Auto-generated constructor stub
    }

    // //////////////////////////////////Toast//////////////////////////////////////

    /**
     * 显示Toast
     *
     * @param context Context
     * @param text    String
     */
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast
     *
     * @param context Context
     * @param resId   int
     */
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    // //////////////////////////////////Shake//////////////////////////////////////

    /**
     * 设置晃动动画
     */
    public static void setShakeAnimation(View view, int counts) {
        view.startAnimation(shakeAnimation(counts));
    }

    /**
     * 设置晃动动画(Default:5次晃动)
     */
    public static void setShakeAnimation(View view) {
        view.startAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return Animation
     */
    private static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    // //////////////////////////////////SoftKeyboard//////////////////////////////////////

    /**
     * 隐藏键盘
     *
     * @param activity Activity
     */
    public static boolean hideSoftKeyboard(Activity activity) {
        if (activity == null) {
            return false;
        }

        if (activity.getCurrentFocus() != null) {
            IBinder ib = activity.getCurrentFocus().getWindowToken();
            if (null != ib) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm) {
                    return imm.hideSoftInputFromWindow(ib, 0);
                }
            }
        }
        return false;
    }

    /**
     * 显示键盘
     *
     * @param activity Activity
     */
    public static void showSoftKeyboard(final Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Service.INPUT_METHOD_SERVICE);
                if (null != imm) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 30);
    }

    /**
     * 强制显示键盘
     *
     * @param activity Activity
     * @param v        View
     */
    public static void showSoftKeyboard(Activity activity, View v) {
        if (activity == null || v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制隐藏键盘
     *
     * @param activity Activity
     * @param v        View
     */
    public static void hideSoftKeyboard(Activity activity, View v) {
        if (activity == null || v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    // //////////////////////////////////Share//////////////////////////////////////

    /**
     * 分享
     *
     * @param activity Activity
     * @param text     String
     * @param imgFile  File
     */
    public static void startShareIntent(Activity activity, String text, File imgFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(text)) {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);

        }
        if (imgFile != null && imgFile.exists()) {
            Uri uri = Uri.fromFile(imgFile);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        activity.startActivity(Intent.createChooser(intent, "分享至"));
    }

    // //////////////////////////////////Assets//////////////////////////////////////

    /**
     * 从assets下文件中读取String
     *
     * @param context  Context
     * @param fileName String
     * @return String
     */
    public static String getStringFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int length = in.available();
            // 创建byte数组
            byte[] buffer = new byte[length];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 设置TextView可选择、复制
     *
     * @param tv TextView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setTextViewSelectAndCopy(TextView tv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tv.setTextIsSelectable(true);
        } else {
            tv.setFocusable(true);
            tv.setClickable(true);
            tv.setLongClickable(true);
            tv.setFocusableInTouchMode(true);
            tv.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            tv.setText(tv.getText(), BufferType.SPANNABLE);
        }
    }

    /**
     * 设置高亮词
     *
     * @param context Context
     * @param target  要设置的高亮词
     * @param entire  全部字符串
     * @param colorId 要设置的高亮色资源id
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder makeHighLight(Context context,
                                                       String target, String entire, int colorId) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(entire);
        if (!TextUtils.isEmpty(target) && !TextUtils.isEmpty(entire)) {
            int start = entire.indexOf(target);
            if (start == -1) {
                return ssb;
            }
            int end = start + target.length();
            int color = context.getResources().getColor(colorId);
            ForegroundColorSpan blackSpan = new ForegroundColorSpan(color);
            ssb.setSpan(blackSpan, start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }

    /**
     * @param context Context
     * @param targets 要设置的高亮词组
     * @param entire  全部字符串
     * @param colorId 要设置的高亮色资源id
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder makeHighLight(Context context,
                                                       String[] targets, String entire, int colorId) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(entire);
        if (targets == null || targets.length == 0) {
            return ssb;
        }
        for (String target : targets) {
            if (!TextUtils.isEmpty(target) && !TextUtils.isEmpty(entire)) {
                int start = entire.indexOf(target);
                if (start == -1) {
                    continue;
                }
                int end = start + target.length();
                int color = context.getResources().getColor(colorId);
                ForegroundColorSpan blackSpan = new ForegroundColorSpan(color);
                ssb.setSpan(blackSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ssb;
    }

    /**
     * @param context Context
     * @param splits  分割词组
     * @param entire  全部字符串
     * @param colorId 要设置的高亮色资源id
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder makeHighLightWithSplit(
            Context context, String[] splits, String target, String entire,
            int colorId) {
        if (target == null)
            target = "";
        SpannableStringBuilder ssb = new SpannableStringBuilder(entire);
        if (splits != null && splits.length != 0) {
            for (String split : splits) {
                target = target.replace(split, "#");
            }
            String[] targets = target.split("#");
            if (targets.length > 0) {
                ssb = makeHighLight(context, targets, entire, colorId);
            }
        }
        return ssb;
    }

    // //////////////////////////////////Secret//////////////////////////////////////
    private static String getAESSeed(Context context) {
        String seed;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String ssid = tm.getSubscriberId(); // 支付宝用这个ID的前8位做加密密钥
        if (!TextUtils.isEmpty(ssid) && ssid.length() >= 8) {
            seed = ssid.substring(0, 8);
        } else {
            seed = Constants.ENCRYPT_SEED;
        }
        return seed;
    }

    public static String AESDecrypt(Context context, String ciphertext) {
        String seed = "", text = "";
        if (TextUtils.isEmpty(ciphertext)) {
            return text;
        }
        try {
            seed = getAESSeed(context);
            text = AES.decrypt(seed, ciphertext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    public static String AESEncrypt(Context context, String cleartext) {
        String seed = "", ciphertext = "";
        if (TextUtils.isEmpty(cleartext)) {
            return ciphertext;
        }
        try {
            seed = getAESSeed(context);
            ciphertext = AES.encrypt(seed, cleartext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ciphertext;
    }

    // //////////////////////////////////Other//////////////////////////////////////

    /**
     * check the app is installed
     *
     * @param context     Context
     * @param packagename App包名
     * @return boolean
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        return packageInfo != null;
    }

    /**
     * * 设置添加屏幕的背景透明度
     *
     * @param activity 当前的Activity
     * @param bgAlpha  窗口背景alpha(0.0-1.0)
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = bgAlpha;
        window.setAttributes(lp);
    }

    /**
     * Get App VersionCode
     *
     * @param context Context
     * @return int
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get App VersionName
     *
     * @param context Context
     * @return String
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 使系统产生震动
     *
     * @param context Context
     */
    public static void makeVibrate(Context context) {
        if (context == null) {
            return;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        vibrator.vibrate(new long[]{0, 1, 20, 30}, -1);
    }

    /**
     * 手机号分割显示
     *
     * @param mobile    手机号
     * @param separator 分隔符
     * @return String
     */
    public static String makeMobleSection(String mobile, String separator) {
        if (TextUtils.isEmpty(mobile)) {
            return null;
        }
        if (TextUtils.isEmpty(separator)) {
            return mobile;
        }
        int length = mobile.length();
        if (length <= 3) {
            return mobile;
        }
        StringBuilder sb = new StringBuilder();
        if (length > 3 && length <= 7) {
            String first = mobile.substring(0, 3);
            sb.append(first);
            sb.append(separator);
            String second = mobile.substring(3, 8);
            sb.append(second);
        }
        if (length > 7) {
            String first = mobile.substring(0, 3);
            sb.append(first);
            sb.append(separator);
            String second = mobile.substring(3, 7);
            sb.append(second);
            sb.append(separator);
            String third = mobile.substring(7, length);
            sb.append(third);
        }
        return sb.toString();
    }

    /**
     * 处理头像url是否添加缩略图后缀
     *
     * @param headUrl String
     * @param postfix String
     * @return String
     */
//    public static String executeHeadUrlPostfix(String headUrl, String postfix) {
//        if (TextUtils.isEmpty(headUrl) || TextUtils.isEmpty(postfix)) {
//            return headUrl;
//        }
//        String upyunHost = BACommon.getUpyunHost();
//        if (TextUtils.isEmpty(upyunHost)) {
//            return headUrl;
//        } else {
//            if (!headUrl.contains(upyunHost)) {
//                return headUrl;
//            } else {
//                return headUrl + postfix;
//            }
//        }
//
//    }

    /**
     * 通用提示框
     *
     * @param context  Context
     * @param resId    int
     * @param isFinish boolean
     */
    public static void showCustomDialog(Context context, int resId, final boolean isFinish) {
        String message = context.getResources().getString(resId);
        if (!TextUtils.isEmpty(message)) {
            showCustomDialog(context, message, isFinish);
        }
    }

    /**
     * 通用提示框
     *
     * @param context Context
     * @param message String
     */

    public static void showCustomDialog(final Context context, String message, final boolean isFinish) {
        // TODO Auto-generated method stub
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
        builder.setTitle(R.string.alert);
        builder.setMessageGravity(Gravity.CENTER);
        builder.setMessage(message);
        builder.setMessageColor(context.getResources().getColor(R.color.color_1));
        builder.setNeutralButton(R.string.i_see, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isFinish && context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                }
        );
        ConfirmDialog dlg = builder.create();
        dlg.setCancelable(true);
        dlg.setCanceledOnTouchOutside(true);
        Window window = dlg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.2f;
        window.setAttributes(lp);
        dlg.show();
    }

}
