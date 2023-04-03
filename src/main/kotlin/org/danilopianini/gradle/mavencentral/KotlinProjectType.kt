package org.danilopianini.gradle.mavencentral

import java.util.Locale

/**
 * The type of Kotlin project (JVM, Js, or Multiplatform, depending on which Gradle plugin is applied).
 */
enum class KotlinProjectType {
    JVM, JS, MULTI_PLATFORM;

    /**
     * The full ID of the Kotlin plugin.
     */
    val pluginId: String by lazy { "org.jetbrains.kotlin.${name.lowercase(Locale.getDefault())}" }
}
