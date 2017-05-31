# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/tianshutong/Desktop/android_develop/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 保持哪些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**

-keep class android.net.** {*;}
-keep class android.net.compatibility.** {*;}
-keep class android.net.http.** {*;}
-keep class com.android.internal.http.multipart.** {*;}
-keep class org.apache.commons.** {*;}
-keep class org.apache.http.** {*;}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#自定义删除
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

# lambda
-keep public class java.lang.invoke.** {*;}
-dontwarn java.lang.invoke.**
-keepclassmembernames class * {
    private static synthetic *** lambda$*(...);
}

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements android.os.Parcelable {
    public <fields>;
    private <fields>;
}

# butter knife
-keepattributes *Annotation*
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# databaseorm
-keep public class * extends com.orm.androrm.Model
-keepclasseswithmembernames  class * extends com.orm.androrm.Model {
    public <fields>;
}
-keepclasseswithmembernames class com.gongchang.xizhi.vo.** {
    public <fields>;}
-keep class com.orm.androrm.** {*;}
-dontwarn org.apache.commons.lang3.StringUtils