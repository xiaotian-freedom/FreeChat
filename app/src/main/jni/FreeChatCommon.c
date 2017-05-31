#include "com_storn_freechat_jni_XMPPCommon.h"

JNIEXPORT jstring JNICALL Java_com_storn_freechat_jni_FreeChatCommon_getXMPPHost
    (JNIEnv *env, jobject obj) {
//    return (*env)->NewStringUTF(env, "192.168.31.159");
    return (*env)->NewStringUTF(env, "172.16.14.103");
}

JNIEXPORT jstring JNICALL Java_com_storn_freechat_jni_FreeChatCommon_getXMPPServerName
    (JNIEnv *env, jobject obj) {
    return (*env)->NewStringUTF(env, "freechat.storn.com");
}

JNIEXPORT jint JNICALL Java_com_storn_freechat_jni_FreeChatCommon_getXMPPPort
    (JNIEnv *env, jobject obj){
    return 5222;
}