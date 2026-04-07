plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // 添加 Hilt，Hilt 依赖 KSP，所以先安装 KSP
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    // 新增：kapt 用于 DataBinding，DataBinding 目前尚未迁移至 KSP
    kotlin("kapt")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.green_agriculture"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.green_agriculture"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.7") {
        exclude(group = "xmlpull", module = "xmlpull")
    }
    
    // Hilt 与 Navigation 集成
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation("androidx.hilt:hilt-navigation:1.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    ksp("com.google.dagger:hilt-compiler:2.57.1")

    implementation("com.github.bumptech.glide:glide:5.0.5")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}