# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/marcel/Android/Sdk/tools/proguard/proguard-android.txt
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

#Line numbers
-ignorewarnings
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#NetGuard
-keepnames class inc.bitwise.vpnapp.** { *; }

#Hathway SDKs
-keep public class inc.bitwise.vpnazure.**
-keepclassmembers class inc.bitwise.vpnazure.**{
    public *;
}

#JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

#JNI callbacks
-keep class inc.bitwise.vpnapp.Allowed { *; }
-keep class inc.bitwise.vpnapp.Packet { *; }
-keep class inc.bitwise.vpnapp.ResourceRecord { *; }
-keep class inc.bitwise.vpnapp.Usage { *; }
-keep class inc.bitwise.vpnapp.ServiceSinkhole {
    void nativeExit(java.lang.String);
    void nativeError(int, java.lang.String);
    void logPacket(inc.bitwise.vpnapp.Packet);
    void dnsResolved(inc.bitwise.vpnapp.ResourceRecord);
    boolean isDomainBlocked(java.lang.String);
    eu.faircode.netguard.Allowed isAddressAllowed(inc.bitwise.vpnapp.Packet);
    void accountUsage(inc.bitwise.vpnapp.Usage);
}

#AndroidX
-keep class androidx.appcompat.widget.** { *; }
-keep class androidx.appcompat.app.AppCompatViewInflater { <init>(...); }
-keepclassmembers class * implements android.os.Parcelable { static ** CREATOR; }

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep enum com.bumptech.glide.** {*;}
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}

#AdMob
-dontwarn com.google.android.gms.internal.**

# ButterKnife
-keep public class * implements butterknife.internal.ViewBinder { public <init>(); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**


#RxJava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

# Parcel library
-keep class **$$Parcelable { *; }
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keep class android.support.v4.** { *; }

    -dontwarn javax.security.**
    -dontwarn java.awt.**
    -dontwarn java.beans.Beans
    -dontwarn okio.**

    -keep class org.apache.http.** { *; }
    -keepclassmembers class org.apache.http.** {*;}
    -dontwarn org.apache.**

    -keep class android.net.http.** { *; }
    -keepclassmembers class android.net.http.** {*;}

    -keep class org.eclipse.paho.client.**{*;}
    -keepclassmembers class org.eclipse.paho.client.**{*;}

    -dontwarn android.net.**
    -dontnote
    -dontoptimize
    -dontpreverify
    -dontwarn com.squareup.okhttp.**

    -keep class javamail.** {*;}
    -keep class javax.mail.** {*;}
    -keep class javax.activation.** {*;}
    -keep class com.sun.mail.dsn.** {*;}
    -keep class com.sun.mail.handlers.** {*;}
    -keep class com.sun.mail.smtp.** {*;}
    -keep class com.sun.mail.util.** {*;}
    -keep class mailcap.** {*;}
    -keep class mimetypes.** {*;}
    -keep class myjava.awt.datatransfer.** {*;}
    -keep class org.apache.harmony.awt.** {*;}
    -keep class org.apache.harmony.misc.** {*;}