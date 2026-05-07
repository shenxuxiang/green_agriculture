import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// processor/build.gradle.kts
plugins {
    kotlin("jvm")
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.2.10-2.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}