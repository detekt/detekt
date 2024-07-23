package dev.detekt.gradle.plugin.internal

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.VerificationException
import org.gradle.util.GradleVersion

internal fun ProviderFactory.gradlePropertyAtConfigTimeCompat(propertyName: String): Provider<String> =
    if (GradleVersion.current() >= GradleVersion.version("7.4")) {
        gradleProperty(propertyName)
    } else {
        @Suppress("DEPRECATION")
        gradleProperty(propertyName).forUseAtConfigurationTime()
    }

internal fun Project.projectDirectoryCompat(): Directory =
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

internal fun throwVerificationException(message: String, cause: Throwable): Nothing =
    when {
        GradleVersion.current() >= GradleVersion.version("8.2") -> throw VerificationException(message, cause)
        GradleVersion.current() >= GradleVersion.version("7.4.2") -> throw VerificationException(message)
        else -> throw GradleException(message, cause)
    }
