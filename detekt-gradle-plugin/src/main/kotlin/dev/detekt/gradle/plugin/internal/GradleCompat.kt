package dev.detekt.gradle.plugin.internal

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
