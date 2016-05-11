# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/rmateus/android-sdk-linux/tools/proguard/proguard-android.txt
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

#Tests only
-dontwarn com.google.android.gms.**
-dontwarn com.octo.android.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-keep class com.paypal.**
-dontwarn com.paypal.**
-dontobfuscate
-keepattributes *Annotation*,EnclosingMethod

-keepnames class * { @butterknife.InjectView *;}
-dontwarn butterknife.Views$InjectViewProcessor

-keep class android.support.v7.appcompat.** { *; }

-ignorewarnings

#Crashlytics
-keepattributes SourceFile,LineNumberTable

####### RetroLambda #######
-dontwarn java.lang.invoke.*

####### Retrofit #######
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.**
-dontwarn okio.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-dontwarn com.rabbitmq.**

####### RxAndroid #######
-dontwarn rx.internal.util.unsafe.**

####### AppCompat #######
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.design.widget.** { *; }

-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-keep class android.support.v4.widget.DrawerLayout {*;}
-keep class android.support.v4.widget.FixedDrawerLayout {*;}
-keep class android.support.v4.widget.ViewDragHelper {*;}

-keep public class android.support.v4.widget.** { *; }
-keep public class android.support.v4.internal.widget.** { *; }
-keep public class android.support.v4.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}


####### Glide #######
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

####### ButterKnife #######
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


-dontwarn com.fasterxml.jackson.databind.**



####### Jackson2 #######
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}

-keepnames class com.fasterxml.jackson.** { *; }
#-keepclassmembers,allowobfuscation class * {
#    @org.codehaus.jackson.annotate.* <fields>;
#    @org.codehaus.jackson.annotate.* <init>(...);
#}
-keep class com.aptoide.dataprovider.webservices.models.** { *; }

-keep class com.aptoide.amethyst.** {*;}
-keep class com.aptoide.** {*;}
-keep class cm.aptoide.** {*;}

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * implements java.io.Serializable {
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

-keep class android.support.design.widget.AppBarLayout$Behavior$SavedState { *; }
-keep class android.support.design.widget.CoordinatorLayout$SavedState { *; }

#-keep class com.octo.android.robospice.retrofit.** { *; }