pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://dl.google.com/dl/android/maven2/")
        maven("https://maven.myket.ir") {
            content {
                includeGroupByRegex("androidx\\..*")
            }
        }
    }
}


gradle.settingsEvaluated {
    fun RepositoryHandler.kobwebSnapshots() {
        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            mavenContent {
                includeGroupByRegex("com\\.varabyte\\.kobweb.*")
                snapshotsOnly()
            }
        }
    }

    pluginManagement.repositories { kobwebSnapshots() }
    dependencyResolutionManagement.repositories {
        kobwebSnapshots()
    }
}

rootProject.name = "easypeayenglish"

include(":site")
