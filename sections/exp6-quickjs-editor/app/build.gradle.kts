plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "cn.qsbye.quickjseditor"
    compileSdk = 36

    defaultConfig {
        applicationId = "cn.qsbye.quickjseditor"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "26.10.20.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")

    // Manifest 主题 @style/Theme.AppCompat.DayNight.NoActionBar 需要 appcompat
    implementation("androidx.appcompat:appcompat:1.7.0")

    // QuickJS（Apache-2.0）：端上执行用户绘图脚本
    implementation("io.github.taoweiji.quickjs:quickjs-android:1.4.6")
}
