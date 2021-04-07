package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.util.GradleVersion
import java.io.File
import javax.inject.Inject

open class DetektExtension @Inject constructor(objects: ObjectFactory) : CodeQualityExtension() {

    var ignoreFailures: Boolean
        @JvmName("ignoreFailures_")
        get() = isIgnoreFailures

        @JvmName("ignoreFailures_")
        set(value) {
            isIgnoreFailures = value
        }

    val customReportsDir: File?
        get() = reportsDir

    val reports = DetektReports()

    var input: ConfigurableFileCollection = objects.fileCollection()
        .from(
            DEFAULT_SRC_DIR_JAVA,
            DEFAULT_TEST_SRC_DIR_JAVA,
            DEFAULT_SRC_DIR_KOTLIN,
            DEFAULT_TEST_SRC_DIR_KOTLIN,
        )

    var baseline: File? = objects.fileProperty()
        .run {
            if (GradleVersion.current() < GradleVersion.version("6.0")) {
                set(File("baseline.xml"))
                this
            } else {
                fileValue(File("baseline.xml"))
            }
        }
        .get().asFile

    var basePath: String? = null

    var config: ConfigurableFileCollection = objects.fileCollection()

    var debug: Boolean = DEFAULT_DEBUG_VALUE

    var parallel: Boolean = DEFAULT_PARALLEL_VALUE

    @Deprecated("Please use the buildUponDefaultConfig and allRules flags instead.", ReplaceWith("allRules"))
    var failFast: Boolean = DEFAULT_FAIL_FAST_VALUE

    var allRules: Boolean = DEFAULT_ALL_RULES_VALUE

    var buildUponDefaultConfig: Boolean = DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE

    var disableDefaultRuleSets: Boolean = DEFAULT_DISABLE_RULESETS_VALUE

    var autoCorrect: Boolean = DEFAULT_AUTO_CORRECT_VALUE

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

    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    companion object {
        const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
        const val DEFAULT_TEST_SRC_DIR_JAVA = "src/test/java"
        const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
        const val DEFAULT_TEST_SRC_DIR_KOTLIN = "src/test/kotlin"
        const val DEFAULT_DEBUG_VALUE = false
        const val DEFAULT_PARALLEL_VALUE = false
        const val DEFAULT_AUTO_CORRECT_VALUE = false
        const val DEFAULT_DISABLE_RULESETS_VALUE = false
        const val DEFAULT_REPORT_ENABLED_VALUE = true
        const val DEFAULT_FAIL_FAST_VALUE = false
        const val DEFAULT_ALL_RULES_VALUE = false
        const val DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE = false
    }
}
