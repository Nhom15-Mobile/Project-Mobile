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
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}


rootProject.name = "APP-ANDROID"

// App chính
include(":app")

// Core (dùng chung)
include(":core:ui")
include(":core:network")
include(":core:db")
include(":core:util")

// Domain (logic thuần)
include(":domain")

// Feature (tính năng)
include(":feature:auth")
include(":feature:appointment")
include(":feature:payment")
include(":feature:records")
include(":feature:profile")
include(":feature:notification")
