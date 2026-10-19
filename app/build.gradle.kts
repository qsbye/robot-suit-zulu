import java.time.LocalDateTime

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

// 版本号规则: 年(2位).月.日.时，按构建时间自动生成
val buildNow = LocalDateTime.now()
val generatedVersionName =
    "${buildNow.year % 100}.${buildNow.monthValue}.${buildNow.dayOfMonth}.${buildNow.hour}"
val generatedVersionCode =
    (buildNow.year % 100) * 1_000_000 + buildNow.monthValue * 10_000 + buildNow.dayOfMonth * 100 + buildNow.hour

android {
    namespace = "cn.qsbye.robot_suit_zulu"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.qsbye.robot_suit_zulu"
        minSdk = 24
        targetSdk = 34
        versionCode = generatedVersionCode
        versionName = generatedVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // CameraX（绘画页 FOSS 版 AR 画线取景，Apache-2.0）
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    // ZXing（二维码定位页，Apache-2.0）
    implementation(libs.zxing.core)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}