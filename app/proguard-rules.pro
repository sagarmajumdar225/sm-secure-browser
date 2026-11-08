# GeckoView keep rules
-keep class org.mozilla.geckoview.** { *; }
-keep interface org.mozilla.geckoview.** { *; }

# SQLCipher keep rules
-keep class net.sqlcipher.** { *; }
-keep class net.zetetic.** { *; }

# Room Database keep rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlin Coroutines & Serialization
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Suppress harmless warnings from GeckoView, SnakeYAML, & SQLCipher native libs
-dontwarn java.beans.**
-dontwarn org.yaml.snakeyaml.**
-dontwarn org.mozilla.geckoview.**
-dontwarn net.sqlcipher.**
-dontwarn net.zetetic.**
