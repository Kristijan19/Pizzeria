# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Facebook
-keep class com.facebook.** { *; }
-keep class com.facebook.login.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Model klasi
-keep class com.anas.pizzeria.model.** { *; }
-keep class com.anas.pizzeria.data.local.entity.** { *; }