package com.storn.freechat.jni;

/**
 * Created by tianshutong on 2017/3/27.
 */

public class FreeChatCommon {
    static {
        System.loadLibrary("FreeChatCommon");
    }

    public static native String getXMPPHost();

    public static native String getXMPPServerName();

    public static native int getXMPPPort();
}
