package dev.detekt.gradle.plugin.internal

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.tasks.VerificationException
import org.gradle.util.GradleVersion

internal fun Project.rootProjectDirectoryCompat(): Directory =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        isolated.rootProject.projectDirectory
    } else {
        rootProject.layout.projectDirectory
    }

internal fun ConfigurableFileCollection.conventionCompat(paths: Iterable<*>): ConfigurableFileCollection =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        convention(paths)
    } else {
        setFrom(paths)
        this
    }

internal fun ConfigurableFileCollection.conventionCompat(vararg paths: Any): ConfigurableFileCollection =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        convention(paths)
    } else {
        setFrom(paths)
        this
    }

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

@Suppress("NOTHING_TO_INLINE") // not inlining for performance, but for simpler stack traces
internal inline fun verificationExceptionCompat(message: String, cause: Throwable): GradleException =
    if (GradleVersion.current() >= GradleVersion.version("8.2")) {
        VerificationException(message, cause)
    } else {
        VerificationException(message)
    }
