# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK's proguard-android-optimize.txt file.

# Keep data classes used for serialization
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
