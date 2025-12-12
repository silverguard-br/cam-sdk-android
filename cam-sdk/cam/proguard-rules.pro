# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Mantém anotações
-keepattributes *Annotation*

# Mantém classes do modelo usadas por JSON (Gson)
-keep class com.silverguard.cam.core.model.** { *; }

# Regras mínimas para Gson funcionar
-keepattributes Signature
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Regras mínimas para Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn javax.annotation.**
-dontwarn okio.**

# Regras mínimas para Kotlin
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }

# Remove logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Mantém apenas as APIs públicas do SDK
-keep public class com.silverguard.cam.core.config.SilverguardCam { public *; }
-keep public class com.silverguard.cam.CamMainActivity { public *; }

# Mantém interfaces de estilo públicas do SDK
-keep interface com.silverguard.cam.core.styles.** { *; }

# Garante que enums públicos do SDK não sejam ofuscados ou removidos
-keep enum com.silverguard.cam.core.config.ENVIRONMENT { *; }
-keep enum com.silverguard.cam.core.config.FLOW { *; }

# Garante que os métodos e campos públicos do objeto Kotlin SilverguardCam sejam preservados
-keep class com.silverguard.cam.core.config.SilverguardCam { public *; }
-keepclassmembers class com.silverguard.cam.core.config.SilverguardCam {
    public static *;
}

# Mantém possíveis constantes públicas e getters gerados (ex.: getBaseUrl)
-keepclassmembers class com.silverguard.cam.core.config.SilverguardCam {
    public <methods>;
    public <fields>;
}

# Mantém Activities e Fragments do SDK (usados via reflexão / Navigation)
-keep class com.silverguard.cam.**Activity { *; }
-keep class com.silverguard.cam.**Fragment { *; }
-keep class com.silverguard.cam.ui.** { *; }

# Também garante que qualquer Fragment do projeto não seja removido
-keep class * extends androidx.fragment.app.Fragment { *; }

# Ignora warnings de classes Java 9+
-dontwarn java.lang.invoke.**