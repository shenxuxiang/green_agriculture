import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// annotation/build.gradle.kts
plugins {
    kotlin("jvm")
}

// 方案一：使用 jvmToolchain（推荐）
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}