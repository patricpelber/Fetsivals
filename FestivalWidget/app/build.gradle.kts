plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.festivalwidget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.festivalwidget"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            // Shrinks code + resources so the installed app stays tiny.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Signed with the debug key so the release APK installs directly.
            // Replace with your own keystore before publishing.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

// Intentionally NO dependencies: no AppCompat, Material, Compose or Glance.
// Widgets are plain RemoteViews, which is the lightest option on Android.
