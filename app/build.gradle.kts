import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

fun buildVersionCode(): Int {
    val taskNames = gradle.startParameter.taskNames
    val taskName = taskNames.find { it.startsWith("assemble") } ?: ""
    val pattern = """^assemble(?<falvorName>[A-Z][a-z]+)(?<buildType>([A-Z][a-z]+))$""".toRegex()

    return pattern.matchEntire(taskName)?.let { matchResult ->
        val flavorName = matchResult.groups["falvorName"]?.value?.lowercase()
        val buildType = matchResult.groups["buildType"]?.value?.lowercase()

        if (buildType == "debug") return 1

        val propertyFile =
            File("${project.projectDir.path}/properties/${flavorName}_${buildType}.properties")
        val props = Properties()
        if (propertyFile.exists()) {
            props.load(FileInputStream(propertyFile))
        } else {
            props["versionCode"] = "0"
            propertyFile.parentFile?.mkdirs()
            props.store(FileOutputStream(propertyFile), null)
        }

        val nextVersionCode = (props["versionCode"] as String).toInt() + 1
        props["versionCode"] = nextVersionCode.toString()
        props.store(FileOutputStream(propertyFile), null)
        nextVersionCode
    } ?: 1
}

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


        resValue(type = "string", name = "app_name", value = "绿农")

        // 构建 VersionCode
        val vCode = buildVersionCode()
        versionCode = vCode
        versionName = "1.0.${vCode}"
        println("=============== Build V$versionName ===============")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("./debug-keystore.jks")
            keyAlias = "debug-keystore"
            storePassword = "sqal@145680"
            keyPassword = "sqal@145680"
        }
        create("release") {
            storeFile = file("./release-keystore.jks")
            keyAlias = "release-keystore"
            storePassword = "sqal@145680"
            keyPassword = "sqal@145680"
        }
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            // 开发、测试环境
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = """"http://60.169.69.3:30062""""
            )
            resValue(name = "app_name", type = "string", value = "绿农Dev")
        }
        create("staging") {
            // 预发布环境
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = """"http://60.169.69.3:30062""""
            )
            resValue(name = "app_name", type = "string", value = "绿农Staging")
        }
        create("prod") {
            // 生产环境
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = """"http://60.169.69.3:30066""""
            )
            resValue(name = "app_name", type = "string", value = "绿农")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }

        release {
            isMinifyEnabled = false

            signingConfig = signingConfigs.getByName("release")
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

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    // 遍历所有的变体，applicationVariants 是一个表示应用变体的集合（例如，debug、release等）。
    // 注意，applicationVariants 表示当前配置中所有的变体，并不是当前构建的变体。
    applicationVariants.all {
        // 获取变体的构建类型、产品风味、版本名称
        val buildType = this.buildType.name
        val flavorName = this.flavorName
        val versionName = this.versionName
        // 每个变体可能有多个输出（例如，多个 APK、AAB 等）。
        outputs.all {
            // 类型检查，确保处理的是 APK 输出。
            // 注意：BaseVariantOutputImpl 类型表示 APK 输出，这个类是一个内部 API。
            if (this is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                // 查找过滤器类型为 ABI 的过滤器
                // filter?.identifier 就是我们想要的 apk 的格式：比如 arm64-v8a
                val abiType = this.filters.find { it.filterType == "ABI" }?.identifier ?: ""
                this.outputFileName =
                    "app-${flavorName}-${buildType}-${abiType}-v${versionName}.apk"
            }
        }
    }

//    kotlinOptions {
//        jvmTarget = "11"
//    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
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