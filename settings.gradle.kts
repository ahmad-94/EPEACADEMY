pluginManagement {
    repositories {
//         Mirrors first — AGP (com.android.tools.build:*) is resolved here
//        maven("https://maven.myket.ir")
//        maven("https://maven.aliyun.com/repository/google")
//        maven("https://maven.aliyun.com/repository/gradle-plugin")
//        maven("https://maven.aliyun.com/repository/public")

        mavenCentral()
        google()
        gradlePluginPortal()

        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            mavenContent {
                includeGroupByRegex("com\\.varabyte\\.kobweb.*")
                snapshotsOnly()
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()

        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            mavenContent {
                includeGroupByRegex("com\\.varabyte\\.kobweb.*")
                snapshotsOnly()
            }
        }
    }
}

rootProject.name = "easypeayenglish"

include(":site")
include(":androidapp")
