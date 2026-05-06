// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.android.build.gradle.BaseExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // 添加如下两行代码，注意 kotlin 的版本和 KSP 版本必须对应，否则无法下载
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
    id("com.google.dagger.hilt.android") version "2.57.1" apply false
    id("androidx.navigation.safeargs") version "2.9.7" apply false
}

subprojects {
    pluginManager.withPlugin("com.android.base") {
        extensions.configure<BaseExtension> {
            lintOptions.apply {
                lintConfig = rootProject.file("lint.xml")
                htmlOutput = rootProject.file("build/reports/lint-${project.name}.html")
                xmlOutput = rootProject.file("build/reports/lint-${project.name}.xml")
                isCheckDependencies = false
                isCheckReleaseBuilds = true
                isAbortOnError = false
            }
        }
    }
}
