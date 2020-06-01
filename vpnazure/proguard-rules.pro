# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { ; }
-dontwarn javax.*
-dontwarn io.realm.**

-keep class android.support.v4.** { *; }

    -dontwarn javax.security.**
    -dontwarn java.awt.**
    -dontwarn java.beans.Beans

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