package dev.detekt.gradle.plugin.internal

import org.gradle.api.artifacts.Configuration
import org.gradle.util.GradleVersion

/**
 * `Configuration.visible` has no effect from Gradle 9.0 on, its accessors are deprecated and
 * they are scheduled for removal in Gradle 10 - see gradle/gradle#38664.
 */
internal val supportsConfigurationVisibility: Boolean
    get() = GradleVersion.current() < GradleVersion.version("9.0")

internal fun Configuration.setVisibleCompat(visible: Boolean) {
    if (supportsConfigurationVisibility) {
        @Suppress("DEPRECATION")
        isVisible = visible
    }
}
