plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // 添加 Hilt，Hilt 依赖 KSP，所以先安装 KSP
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    // 新增：kapt 用于 DataBinding，DataBinding 目前尚未迁移至 KSP
    kotlin("kapt")

    // 支持 Safe-Args
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
        debug {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = """"http://192.168.5.17:30062""""
            )
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = """"http://60.169.69.3:30066""""
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
        buildConfig = true
    }
}

dependencies {
    // retrofit2 + gson
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // viewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    // 导航支持 Safe-Args
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.7") {
        exclude(group = "xmlpull", module = "xmlpull")
    }

    // Hilt + Navigation 集成，支持 hiltNavGraphViewModels()
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")
    implementation("androidx.hilt:hilt-navigation:1.3.0")

    // Hilt 依赖
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-compiler:2.57.1")

    // Kotlin 反射 API
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Glide
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