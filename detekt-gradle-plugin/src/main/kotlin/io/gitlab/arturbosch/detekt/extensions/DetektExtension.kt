package io.gitlab.arturbosch.detekt.extensions

import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.quality.CodeQualityExtension
import java.io.File

open class DetektExtension(project: Project) : CodeQualityExtension() {

    var ignoreFailures: Boolean
        @JvmName("ignoreFailures_")
        get() = isIgnoreFailures
        @JvmName("ignoreFailures_")
        set(value) = setIgnoreFailures(value)

    val customReportsDir: File?
        get() = reportsDir

    val reports = DetektReports(project)
    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    val idea = IdeaExtension()
    fun idea(configure: Action<IdeaExtension>) = configure.execute(idea)

    var input: ConfigurableFileCollection =
        project.configurableFileCollection().from(DEFAULT_SRC_DIR_JAVA, DEFAULT_SRC_DIR_KOTLIN)

    var baseline: File? = null

    var config: ConfigurableFileCollection = project.configurableFileCollection()

    var debug: Boolean = DEFAULT_DEBUG_VALUE

    var parallel: Boolean = DEFAULT_PARALLEL_VALUE

    var failFast: Boolean = DEFAULT_FAIL_FAST_VALUE

    var buildUponDefaultConfig: Boolean = DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE

    var disableDefaultRuleSets: Boolean = DEFAULT_DISABLE_RULESETS_VALUE

    @Deprecated("Replace with task setIncludes/setExcludes")
    var filters: String? = null

    @Deprecated("Set plugins using the detektPlugins configuration " +
            "(see https://arturbosch.github.io/detekt/extensions.html#let-detekt-know-about-your-extensions)")
    var plugins: String? = null

    companion object {
        const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
        const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
        const val DEFAULT_DEBUG_VALUE = false
        const val DEFAULT_PARALLEL_VALUE = false
        const val DEFAULT_DISABLE_RULESETS_VALUE = false
        const val DEFAULT_REPORT_ENABLED_VALUE = true
        const val DEFAULT_FAIL_FAST_VALUE = false
        const val DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE = false
    }
}
