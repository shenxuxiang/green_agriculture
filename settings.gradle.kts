pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 添加这一行，否则 com.github.promeg:tinypinyin:2.0.3 无法下载
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

rootProject.name = "green_agriculture"
include(":app")
include(":annotation")   // 新增
include(":processor")    // 新增