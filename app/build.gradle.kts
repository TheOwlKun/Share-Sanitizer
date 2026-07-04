import java.util.Properties
import java.io.FileInputStream

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "org.sharesanitizer.app"
  compileSdk = 36

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }

  defaultConfig {
    applicationId = "org.sharesanitizer.app"
    minSdk = 24
    targetSdk = 36
    versionCode = 2
    versionName = "1.0.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
    
    buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
  }

  signingConfigs {
    create("release") {
      val localProps = Properties()
      val localPropsFile = rootProject.file("local.properties")
      if (localPropsFile.exists()) {
          localProps.load(localPropsFile.inputStream())
      }
      val keystorePath = localProps.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH") ?: "${rootDir}/release-key.jks"
      storeFile = file(keystorePath)
      storePassword = localProps.getProperty("KEYSTORE_PASSWORD") ?: System.getenv("KEYSTORE_PASSWORD") ?: "password"
      keyAlias = localProps.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS") ?: "share-sanitizer-key"
      keyPassword = localProps.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD") ?: "password"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      val localProps = Properties()
      val localPropsFile = rootProject.file("local.properties")
      if (localPropsFile.exists()) {
          localProps.load(localPropsFile.inputStream())
      }
      val keystorePath = localProps.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH") ?: "${rootDir}/release-key.jks"
      if (file(keystorePath).exists()) {
          signingConfig = signingConfigs.getByName("release")
      }
    }
    debug {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation("com.github.woheller69:FreeDroidWarn:V1.13")

  testImplementation(libs.junit)
  testImplementation(libs.androidx.junit)

  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)

  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
