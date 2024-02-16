-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepattributes InnerClasses

-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
-keep public class com.google.android.gms.** { public protected *; }
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-dontwarn android.media.Spatializer$OnSpatializerStateChangedListener

-dontwarn android.media.Spatializer
-keep class com.squareup.okhttp.** { *; }

-keep interface com.squareup.okhttp.** { *; }
#startapp
-keep class com.startapp.** {
      *;
}

-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**

-dontwarn org.jetbrains.annotations.**

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum * { *; }
-keep class com.google.code.gson.* { *; }
-keepattributes *Annotation*, Signature, Exception
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#noinspection ShrinkerUnresolvedReference
-keep class com.ironsource.adapters.** { *; }
-dontwarn com.ironsource.**
-dontwarn com.ironsource.adapters.**
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-keep class com.ironsource.adapters.** { *;
}
-keepclassmembers class com.facebook.ads.internal.AdSdkVersion {
    static *;
}
-keepclassmembers class com.facebook.ads.internal.settings.AdSdkVersion {
    static *;
 }
-keepclassmembers class com.facebook.ads.BuildConfig {
    static *;
 }
-keep public interface com.facebook.ads** {*; }
-keep public class com.google.android.gms.** { public protected *; }