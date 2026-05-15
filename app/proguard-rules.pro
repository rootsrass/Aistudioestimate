# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the 
# proguardFiles directive in build.gradle.

# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html

# Keep Hilt and Java standard library
-keep class dagger.hilt.** { *; }
-keep class com.contractorestimator.** { *; }
-keep interface dagger.hilt.** { *; }
-keep class * extends android.app.Application
-keep class * extends android.app.Activity
