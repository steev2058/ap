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

-keepattributes *Annotation* # keep annotations

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.**

-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.gson.** { *; }
-keep class com.apps2you.albaraka.data.model.** { *; }  # Replace with your actual model package

-keep class com.apps2you.albaraka.data.remote.responseModel.** { *; }

-keep class com.apps2you.albaraka.data.remote.repository.** { *; }

# GSON Annotations
-keepclassmembers,allowobfuscation class * {
 @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.apps2you.albaraka.data.remote.networkUtils.MyResponse { *; }
# Keep Retrofit API interfaces
-keep class com.apps2you.albaraka.data.remote.networkUtils.** { *; }
-keep interface com.apps2you.albaraka.data.remote.networkUtils.**

# this class is used as argType in navigation graph
-keep class com.apps2you.albaraka.data.model.FavoriteAccount


-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
    public <init>(...);
 }

# to prevent log messages even in release mode
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
    public static int e(...);
}

# Keep Kotlin metadata
-keepclassmembers class kotlin.Metadata { *; }

# Keep annotations for reflection
-keepattributes RuntimeVisibleAnnotations

# Keep Kotlin class members for coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

-keepattributes SourceFile,LineNumberTable
-dontobfuscate

