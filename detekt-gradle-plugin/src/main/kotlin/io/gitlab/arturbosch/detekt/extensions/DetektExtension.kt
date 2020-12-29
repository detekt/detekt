package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.quality.CodeQualityExtension
import java.io.File
import java.util.function.Predicate
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

    var input: ConfigurableFileCollection =
        objects.fileCollection().from(DEFAULT_SRC_DIR_JAVA, DEFAULT_SRC_DIR_KOTLIN)

    var baseline: File? = null

    var config: ConfigurableFileCollection = objects.fileCollection()

    var debug: Boolean = DEFAULT_DEBUG_VALUE

    var parallel: Boolean = DEFAULT_PARALLEL_VALUE

    var failFast: Boolean = DEFAULT_FAIL_FAST_VALUE

    var buildUponDefaultConfig: Boolean = DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE

    var disableDefaultRuleSets: Boolean = DEFAULT_DISABLE_RULESETS_VALUE

    var autoCorrect: Boolean = DEFAULT_AUTO_CORRECT_VALUE

    /**
     * A filter to specify if the detekt task of any [org.gradle.api.tasks.SourceSet] or
     * [com.android.build.api.dsl.AndroidSourceSet] should be configured and executed given its name.
     */
    var sourceSetFilter: Predicate<String> = Predicate { true }

    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    companion object {
        const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
        const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
        const val DEFAULT_DEBUG_VALUE = false
        const val DEFAULT_PARALLEL_VALUE = false
        const val DEFAULT_AUTO_CORRECT_VALUE = false
        const val DEFAULT_DISABLE_RULESETS_VALUE = false
        const val DEFAULT_REPORT_ENABLED_VALUE = true
        const val DEFAULT_FAIL_FAST_VALUE = false
        const val DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE = false
    }
}
