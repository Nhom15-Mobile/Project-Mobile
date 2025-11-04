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
    }
}

rootProject.name = "Mobile_APP"
include(":app")
include(":ui")
include(":network")
include(":db")
include(":util")
include(":auth")
include(":appointment")
include(":records")
include(":domain")
include(":core:ui", ":core:network", ":core:db", ":core:util")
include(":feature:auth", ":feature:appointment", ":feature:payment", ":feature:records")

include(":payment")
