package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.provider.Property
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.Properties
import javax.inject.Inject

open class DetektExtension @Inject constructor(objects: ObjectFactory) : CodeQualityExtension() {

    init {
        toolVersion = loadDetektVersion(DetektExtension::class.java.classLoader)
    }

    var ignoreFailures: Boolean
        @JvmName("ignoreFailures_")
        get() = isIgnoreFailures
        @JvmName("ignoreFailures_")
        set(value) {
            isIgnoreFailures = value
        }

    val source: ConfigurableFileCollection = objects.fileCollection()
        .from(
            DEFAULT_SRC_DIR_JAVA,
            DEFAULT_TEST_SRC_DIR_JAVA,
            DEFAULT_SRC_DIR_KOTLIN,
            DEFAULT_TEST_SRC_DIR_KOTLIN,
        )

    var baseline: File? = objects
        .fileProperty()
        .fileValue(File("detekt-baseline.xml"))
        .get()
        .asFile

    var basePath: String? = null

    val enableCompilerPlugin: Property<Boolean> =
        objects.property(Boolean::class.java).convention(DEFAULT_COMPILER_PLUGIN_ENABLED)

    val config: ConfigurableFileCollection = objects.fileCollection()

    var debug: Boolean = DEFAULT_DEBUG_VALUE

    var parallel: Boolean = DEFAULT_PARALLEL_VALUE

    var allRules: Boolean = DEFAULT_ALL_RULES_VALUE

    var buildUponDefaultConfig: Boolean = DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE

    var disableDefaultRuleSets: Boolean = DEFAULT_DISABLE_RULESETS_VALUE

    var autoCorrect: Boolean = DEFAULT_AUTO_CORRECT_VALUE

    // TODO: Add test to verify that setting is used from extension and task definition
    var failOnSeverity: String = DEFAULT_FAIL_ON_SEVERITY

    /**
     * List of Android build variants for which no detekt task should be created.
     *
     * This is a combination of build types and flavors, such as fooDebug or barRelease.
     */
    var ignoredVariants: List<String> = emptyList()

    /**
     * List of Android build types for which no detekt task should be created.
     */
    var ignoredBuildTypes: List<String> = emptyList()

    /**
     * List of Android build flavors for which no detekt task should be created
     */
    var ignoredFlavors: List<String> = emptyList()

    companion object {
        const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
        const val DEFAULT_TEST_SRC_DIR_JAVA = "src/test/java"
        const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
        const val DEFAULT_TEST_SRC_DIR_KOTLIN = "src/test/kotlin"
        const val DEFAULT_DEBUG_VALUE = false
        const val DEFAULT_PARALLEL_VALUE = false
        const val DEFAULT_AUTO_CORRECT_VALUE = false
        const val DEFAULT_FAIL_ON_SEVERITY = "error"
        const val DEFAULT_DISABLE_RULESETS_VALUE = false
        const val DEFAULT_REPORT_ENABLED_VALUE = true
        const val DEFAULT_ALL_RULES_VALUE = false
        const val DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE = false

        // This flag is ignored unless the compiler plugin is applied to the project
        const val DEFAULT_COMPILER_PLUGIN_ENABLED = true
    }
}

internal fun loadDetektVersion(classLoader: ClassLoader): String {
    // Other Gradle plugins can also have a versions.properties.
    val distinctVersions = classLoader
        .getResources("detekt-versions.properties")
        .toList()
        .mapNotNull { versions ->
            Properties().run {
                load(versions.openSafeStream())
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
private fun URL.openSafeStream(): InputStream {
    return openConnection()
        /*
         * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
         * it is necessary to disallow caches to maintain stability on JDK 8 and 11 (and possibly more).
         * Otherwise, simultaneous invocations of detekt in the same VM can fail spuriously. A similar bug is referenced in
         * https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
         * Due to https://github.com/detekt/detekt/issues/4332 it is included for all JDKs.
         */
        .apply { useCaches = false }
        .getInputStream()
}
