package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.InputStream
import java.net.URL
import java.util.Properties

@Suppress("ComplexInterface")
interface DetektExtension {

    val toolVersion: Property<String>

    val ignoreFailures: Property<Boolean>

    val failOnSeverity: Property<FailOnSeverity>

    val reportsDir: DirectoryProperty

    val baseline: RegularFileProperty

    val basePath: DirectoryProperty

    val enableCompilerPlugin: Property<Boolean>

    val config: ConfigurableFileCollection

    val debug: Property<Boolean>

    val parallel: Property<Boolean>

    val allRules: Property<Boolean>

    val buildUponDefaultConfig: Property<Boolean>

    val disableDefaultRuleSets: Property<Boolean>

    val autoCorrect: Property<Boolean>

    /**
     * List of Android build variants for which no detekt task should be created.
     *
     * This is a combination of build types and flavors, such as fooDebug or barRelease.
     */
    val ignoredVariants: ListProperty<String>

    /**
     * List of Android build types for which no detekt task should be created.
     */
    val ignoredBuildTypes: ListProperty<String>

    /**
     * List of Android build flavors for which no detekt task should be created
     */
    val ignoredFlavors: ListProperty<String>
}

internal fun loadDetektVersion(classLoader: ClassLoader): String {
    // Other Gradle plugins can also have a versions.properties.
    val distinctVersions = classLoader
        .getResources("detekt-versions.properties")
        .toList()
        .mapNotNull { versions ->
            Properties().run {
                versions.openSafeStream().use(::load)
                getProperty("detektVersion")
            }
        }
        .distinct()
    return distinctVersions.singleOrNull() ?: error(
        "You're importing two detekt plugins which have different versions. " +
            "(${distinctVersions.joinToString()}) Make sure to align the versions."
    )
}

// Copy-paste from io.github.detekt.utils.openSafeStream in Resources.kt.
// Can't use that function, because gradle-plugin is minimising dependencies: see #4748.
private fun URL.openSafeStream(): InputStream =
    openConnection()
        /*
         * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
         * it is necessary to disallow caches to maintain stability on JDK 8 and 11 (and possibly more).
         * Otherwise, simultaneous invocations of detekt in the same VM can fail spuriously. A similar bug is referenced in
         * https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
         * Due to https://github.com/detekt/detekt/issues/4332 it is included for all JDKs.
         */
        .apply { useCaches = false }
        .getInputStream()
