package com.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

    private static final String APP_LOGCAT_FOLDER_NAME = "logcat";

    /* 系统默认的UncaughtException处理类 */
    private UncaughtExceptionHandler mDefaultHandler;

    /* CrashHandler实例,保证只有一个CrashHandler实例 */
    private static volatile CrashHandler instance = null;

    /* 程序的Context对象 */
    private Context mContext;

    private CrashHandler() {

    }

    /* 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param context Context
     * @param isDebug boolean
     */
    public void init(Context context, boolean isDebug) {
        mContext = context;
        if (!isDebug) {
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO Auto-generated method stub
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            ActivityStack activityStack = ActivityStack.getInstance();
//            if (activityStack != null) {
//                activityStack.clearAllActivity();
//            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return true代表处理该异常，不再向上抛异常，
     * false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     * 简单来说就是true不会弹出那个错误提示框，false就会弹出
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        // final String msg = ex.getLocalizedMessage();
        // final StackTraceElement[] stack = ex.getStackTrace();
        // final String message = ex.getMessage();
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序开个小差~_~，即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        saveCrashInfo2File(ex);
//        ActivityStack activityStack = ActivityStack.getInstance();
//        if (activityStack != null) {
//            activityStack.clearAllActivity();
//        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);

        return true;
    }

    private void saveCrashInfo2File(Throwable ex) {
        String fileName;
        FileOutputStream fos = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("*******************************************************************");
            sb.append("\n");
            // 1.发生时间
            long lgCurrent = System.currentTimeMillis();
            sb.append("发生时间：");
            sb.append(TimeUtil.getFormatTimeFromLong(lgCurrent, "yyyy-MM-dd HH:mm:ss"));
            sb.append("\n");

            // 2.当前应用程序信息
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packinfo = pm.getPackageInfo(mContext.getPackageName(), 0);

            // 软件版本
            sb.append("软件版本：").append(packinfo.versionName);
            sb.append("\n");

            // 代码版本
            sb.append("代码版本：");
            sb.append(packinfo.versionCode);
            sb.append("\n");

            // SDK版本
            sb.append("SDK版本：").append(Build.VERSION.SDK_INT);
            sb.append("\n");

            // 手机的硬件信息.
            // sb.append("硬件信息：");
            // sb.append("\n");
            // Field[] fields = Build.class.getDeclaredFields();
            // for (int i = 0; i < fields.length; i++) {
            // // 暴力反射,获取私有的字段信息
            // fields[i].setAccessible(true);
            // String name = fields[i].getName();
            // sb.append(name + " = ");
            // String value = fields[i].get(null).toString();
            // sb.append(value);
            // sb.append("\n");
            // }

            // /////////////////////////////////////////////////////

            // 系统版本
            sb.append("系统版本：");
            sb.append(Build.VERSION.RELEASE);
            sb.append("\n");

            // 手机型号
            sb.append("手机型号：");
            sb.append(Build.MANUFACTURER).append("-").append(Build.MODEL);
            sb.append("\n");

            // 3.获取程序错误的堆栈信息
            sb.append("错误信息：");
            sb.append("\n");
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            ex.printStackTrace(pw);
            Throwable cause = ex.getCause();
            // 循环把所有的异常信息写入writer中
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
            pw.close();// 记得关闭
            String result = writer.toString();
            sb.append(result);

            // 4.保存文件
            fileName = "crash_" + FileUtil.getFileNameByTimeStamp("yyyy-MM-dd") + ".log";
            File dir = FileUtil.getAppExternalCacheDir(APP_LOGCAT_FOLDER_NAME);
            LogUtil.e("CrashHandler", sb.toString());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            fos = new FileOutputStream((new File(dir, fileName)), true);
            fos.write((sb.toString() + "\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
