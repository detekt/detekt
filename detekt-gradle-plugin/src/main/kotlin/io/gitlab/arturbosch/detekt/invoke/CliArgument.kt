package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile

private const val DEBUG_PARAMETER = "--debug"
private const val INPUT_PARAMETER = "--input"
private const val CONFIG_PARAMETER = "--config"
private const val BASELINE_PARAMETER = "--baseline"
private const val PARALLEL_PARAMETER = "--parallel"
private const val DISABLE_DEFAULT_RULESETS_PARAMETER = "--disable-default-rulesets"
private const val BUILD_UPON_DEFAULT_CONFIG_PARAMETER = "--build-upon-default-config"
private const val FAIL_FAST_PARAMETER = "--fail-fast"
private const val PLUGINS_PARAMETER = "--plugins"
private const val REPORT_PARAMETER = "--report"
private const val GENERATE_CONFIG_PARAMETER = "--generate-config"
private const val CREATE_BASELINE_PARAMETER = "--create-baseline"
private const val CLASSPATH_PARAMETER = "--classpath"

internal sealed class CliArgument {
    abstract fun toArgument(): List<String>
}

internal object CreateBaselineArgument : CliArgument() {
    override fun toArgument() = listOf(CREATE_BASELINE_PARAMETER)
}

internal object GenerateConfigArgument : CliArgument() {
    override fun toArgument() = listOf(GENERATE_CONFIG_PARAMETER)
}

internal data class InputArgument(val fileCollection: FileCollection) : CliArgument() {
    override fun toArgument() = listOf(INPUT_PARAMETER, fileCollection.joinToString(",") { it.absolutePath })
}

internal data class ClasspathArgument(val fileCollection: FileCollection) : CliArgument() {
    override fun toArgument() = if (!fileCollection.isEmpty) listOf(
        CLASSPATH_PARAMETER,
        fileCollection.joinToString(";") { it.absolutePath }) else emptyList()
}

internal data class PluginsArgument(val plugins: String?) : CliArgument() {
    override fun toArgument() = plugins?.let { listOf(PLUGINS_PARAMETER, it) } ?: emptyList()
}

internal data class BaselineArgument(val baseline: RegularFile?) : CliArgument() {
    override fun toArgument() = baseline?.let { listOf(BASELINE_PARAMETER, it.asFile.absolutePath) } ?: emptyList()
}

internal data class DefaultReportArgument(val type: DetektReportType, val file: RegularFile?) : CliArgument() {
    override fun toArgument() =
        file?.let { listOf(REPORT_PARAMETER, "${type.reportId}:${it.asFile.absoluteFile}") } ?: emptyList()
}

internal data class CustomReportArgument(val reportId: String, val file: RegularFile) : CliArgument() {
    override fun toArgument() = listOf(REPORT_PARAMETER, "$reportId:${file.asFile.absolutePath}")
}

internal data class ConfigArgument(val config: FileCollection) : CliArgument() {
    override fun toArgument() = if (config.isEmpty) {
        emptyList()
    } else {
        listOf(CONFIG_PARAMETER, config.joinToString(",") { it.absolutePath })
    }
}

internal sealed class BoolCliArgument(open val value: Boolean, val configSwitch: String) : CliArgument() {
    override fun toArgument() = if (value) listOf(configSwitch) else emptyList()
}

internal data class DebugArgument(override val value: Boolean) : BoolCliArgument(value, DEBUG_PARAMETER)

internal data class ParallelArgument(override val value: Boolean) : BoolCliArgument(value, PARALLEL_PARAMETER)
internal data class DisableDefaultRuleSetArgument(override val value: Boolean) :
    BoolCliArgument(value, DISABLE_DEFAULT_RULESETS_PARAMETER)

internal data class BuildUponDefaultConfigArgument(override val value: Boolean) :
    BoolCliArgument(value, BUILD_UPON_DEFAULT_CONFIG_PARAMETER)

internal data class FailFastArgument(override val value: Boolean) : BoolCliArgument(value, FAIL_FAST_PARAMETER)
