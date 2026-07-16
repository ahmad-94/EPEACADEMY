import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.unsafe

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.serialization.plugin)
//    alias(libs.plugins.kobwebx.markdown)
}

group = "com.example.easypeasyenglish"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("The best place to learn English easily. Join our academy today!")
            head.add {
                unsafe {
                    raw("<title>Easy Peasy English- EPE Academy</title>")
                }
                link {
                    rel = "icon"
                    type = "image/png"
                    sizes = "32x32"
                    href = "/icons/epe_academy.png"
                }
                meta {
                    attributes["name"] = "google-site-verification"
                    content = "C0pwLwSDSHG4xXGraUB0j2t8OnsdY1QezmLxepjkz8w"
                }
                meta {
                    attributes["name"] = "msvalidate.01"
                    content = "5B4A3C847AF29EAA787850541A5D3BEC"
                }
                meta {
                    attributes["name"] = "title"
                    content = "Easy Peasy English-EPE Academy"
                }
                meta {
                    attributes["name"] = "description"
                    content = "The best place to learn English easily. Join our academy today!"
                }
                meta {
                    attributes["property"] = "og:title"
                    content = "Easy Peasy English Academy"
                }
                meta {
                    attributes["property"] = "og:description"
                    content = "Learn English easily with our interactive lessons and expert guidance."
                }
                meta {
                    attributes["property"] = "og:image"
                    content = "https://epeacademy.onrender.com/icons/epe_academy.png"
                }
                meta {
                    attributes["property"] = "og:url"
                    content = "https://epeacademy.onrender.com/"
                }
                meta {
                    attributes["property"] = "og:type"
                    content = "website"
                }
                meta {
                    attributes["name"] = "twitter:card"
                    content = "summary_large_image"
                }

                script {
                    src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"
                }
                link {
                    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
                    rel="stylesheet"
                }

                // Highlight.js core (local)
                script {
                    src = "/highlight.min.js"
                }

                // Highlight.js theme (local)
                link {
                    href = "/github-dark.min.css"
                    rel = "stylesheet"
                }
            }
        }

    }
}



kotlin {


    // This example is frontend only. However, for a fullstack app, you can uncomment the includeServer parameter
    // and the `jvmMain` source set below.
    configAsKobwebApplication("easypeayenglish", includeServer = true)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kobwebx.serialization.kotlinx)
            implementation(libs.kotlinx.serialization)
        }

        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa )
            implementation(libs.kobwebx.serialization.kotlinx)
            implementation(libs.kotlinx.serialization)
            // This default template uses built-in SVG icons, but what's available is limited.
            // Uncomment the following if you want access to a large set of font-awesome icons:
            // implementation(libs.silk.icons.fa)
//            implementation(libs.kobwebx.markdown)
        }

        jvmMain.dependencies {
            compileOnly(libs.kobweb.api) // Provided by Kobweb backend at runtime
            implementation(libs.kmongo.database)

            implementation(libs.kobwebx.serialization.kotlinx)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kmongo.serialization)
//            implementation("dnsjava:dnsjava:3.6.4")
        }

    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

