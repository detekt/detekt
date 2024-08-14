package io.gitlab.arturbosch.detekt.internal

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.util.GradleVersion

internal fun ProviderFactory.gradlePropertyAtConfigTimeCompat(propertyName: String): Provider<String> =
    if (GradleVersion.current() >= GradleVersion.version("7.4")) {
        gradleProperty(propertyName)
    } else {
        @Suppress("DEPRECATION")
        gradleProperty(propertyName).forUseAtConfigurationTime()
    }

internal fun Project.rootProjectDirectoryCompat(): Directory =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        isolated.rootProject.projectDirectory
    } else {
        rootProject.layout.projectDirectory
    }
