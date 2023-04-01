@file:Suppress("UnstableApiUsage")

import org.danilopianini.gradle.mavencentral.DocStyle

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.js)
    id("org.danilopianini.publish-on-central")
}

group = "org.danilopianini"
version = "1.0.0"

kotlin {
    js {
        browser()
        nodejs()
        binaries.library()
    }
    sourceSets {
        val main by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

signing {
    if (System.getenv("CI") == "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

publishOnCentral {
    docStyle.set(DocStyle.HTML)
    projectLongName.set("Template for Kotlin Multiplatform Project")
    projectDescription.set("A template repository for Kotlin Multiplatform projects")
    repository("https://maven.pkg.github.com/danysk/${rootProject.name}".toLowerCase()) {
        user.set("DanySK")
        password.set(System.getenv("GITHUB_TOKEN"))
    }
    publishing {
        publications {
            withType<MavenPublication> {
                pom {
                    developers {
                        developer {
                            name.set("Danilo Pianini")
                            email.set("danilo.pianini@gmail.com")
                            url.set("http://www.danilopianini.org/")
                        }
                    }
                }
            }
        }
    }
}
