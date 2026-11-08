import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

android {
  namespace = "com.smsecure.browser"
  compileSdk = 35
  defaultConfig {
    applicationId = "com.smsecure.browser"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
  }

  splits {
    abi {
      isEnable = true
      reset()
      include("arm64-v8a", "armeabi-v7a", "x86_64")
      isUniversalApk = false
    }
  }

  signingConfigs {
    create("release") {
      val localPropsFile = rootProject.file("local.properties")
      val localProps = Properties()
      if (localPropsFile.exists()) {
        localProps.load(localPropsFile.inputStream())
      }

      val storeFileProp = localProps.getProperty("RELEASE_STORE_FILE") ?: System.getenv("RELEASE_STORE_FILE")
      val storePasswordProp = localProps.getProperty("RELEASE_STORE_PASSWORD") ?: System.getenv("RELEASE_STORE_PASSWORD")
      val keyAliasProp = localProps.getProperty("RELEASE_KEY_ALIAS") ?: System.getenv("RELEASE_KEY_ALIAS")
      val keyPasswordProp = localProps.getProperty("RELEASE_KEY_PASSWORD") ?: System.getenv("RELEASE_KEY_PASSWORD")

      if (storeFileProp != null && file(storeFileProp).exists() && storePasswordProp != null && keyAliasProp != null && keyPasswordProp != null) {
        storeFile = file(storeFileProp)
        storePassword = storePasswordProp
        keyAlias = keyAliasProp
        keyPassword = keyPasswordProp
      } else {
        storeFile = getByName("debug").storeFile
        storePassword = getByName("debug").storePassword
        keyAlias = getByName("debug").keyAlias
        keyPassword = getByName("debug").keyPassword
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      signingConfig = if (signingConfigs.getByName("release").storeFile?.exists() == true) {
        signingConfigs.getByName("release")
      } else {
        signingConfigs.getByName("debug")
      }
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    aidl = false
    buildConfig = true
    shaders = false
  }
  packaging {
    jniLibs {
      useLegacyPackaging = true
      keepDebugSymbols.clear()
    }
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86_64" to 3)
androidComponents {
  onVariants { variant ->
    variant.outputs.forEach { output ->
      val abi = output.filters.find { it.filterType == com.android.build.api.variant.FilterConfiguration.FilterType.ABI }?.identifier
      val code = abiCodes[abi]
      if (code != null) {
        output.versionCode.set(code * 10000 + 1)
      }
    }
  }
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)

  // REQUIRED: Compose BOM ensures consistent versions for all Compose libraries. Contains no tracking.
  implementation(composeBom)
  // REQUIRED: Compose BOM ensures consistent versions for tests. Contains no tracking.
  androidTestImplementation(composeBom)

  // REQUIRED: Core Android extensions. Pure local functionality, no network calls.
  implementation(libs.androidx.core.ktx)

  // REQUIRED: Lifecycle components. Pure local functionality, no network calls.
  implementation(libs.androidx.lifecycle.runtime.ktx)

  // REQUIRED: Activity Compose integration. Pure local functionality, no network calls.
  implementation(libs.androidx.activity.compose)

  // REQUIRED: Arch Components Lifecycle for Compose. Pure local functionality, no network calls.
  implementation(libs.androidx.lifecycle.runtime.compose)

  // REQUIRED: ViewModel Compose integration. Pure local functionality, no network calls.
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // REQUIRED: Jetpack Compose UI framework. Pure local functionality, no network calls.
  implementation(libs.androidx.compose.ui)

  // REQUIRED: Compose UI tooling preview. Local design tool, no network calls.
  implementation(libs.androidx.compose.ui.tooling.preview)

  // REQUIRED: Material3 UI components. Pure local functionality, no network calls.
  implementation(libs.androidx.compose.material3)

  // REQUIRED: Debug tooling for Compose. Local design tool, no network calls.
  debugImplementation(libs.androidx.compose.ui.tooling)

  // REQUIRED: Compose UI testing framework. Local testing only, no network calls.
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)

  // REQUIRED: Manifest test tool for Compose. Local testing only, no network calls.
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // REQUIRED: JUnit for unit testing. Local testing only, no network calls.
  testImplementation(libs.junit)

  // REQUIRED: Coroutines testing framework. Local testing only, no network calls.
  testImplementation(libs.kotlinx.coroutines.test)

  // REQUIRED: AndroidX test core. Local testing only, no network calls.
  androidTestImplementation(libs.androidx.test.core)

  // REQUIRED: AndroidX test extension. Local testing only, no network calls.
  androidTestImplementation(libs.androidx.test.ext.junit)

  // REQUIRED: AndroidX test runner. Local testing only, no network calls.
  androidTestImplementation(libs.androidx.test.runner)

  // REQUIRED: Espresso testing framework. Local testing only, no network calls.
  androidTestImplementation(libs.androidx.test.espresso.core)

  // REQUIRED: Jetpack Navigation UI components. Pure local functionality, no network calls.
  implementation(libs.androidx.navigation.compose)

  // REQUIRED: Room database runtime for local history/bookmarks. Pure local functionality, no network calls.
  implementation("androidx.room:room-runtime:2.6.1")

  // REQUIRED: Room database Kotlin extensions. Pure local functionality, no network calls.
  implementation("androidx.room:room-ktx:2.6.1")

  // REQUIRED: Room compiler. Pure local tooling, no network calls.
  ksp("androidx.room:room-compiler:2.6.1")

  // REQUIRED: SQLCipher for encrypting the Room database locally. Pure local cryptography, no network calls.
  implementation("net.zetetic:sqlcipher-android:4.5.4")

  // REQUIRED: DataStore for storing simple settings and preferences. Pure local functionality, no network calls.
  implementation("androidx.datastore:datastore-preferences:1.1.1")

  // REQUIRED: Timber for structured, safe logging. Pure local functionality, no network calls.
  implementation("com.jakewharton.timber:timber:5.0.1")

  // REQUIRED: Mozilla GeckoView (Browser Engine). Core dependency for browsing. Modified via config to prevent tracking.
  implementation("org.mozilla.geckoview:geckoview-omni:130.+")
}
