package io.gitlab.arturbosch.detekt.invoke

import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import java.io.File

private const val DEBUG_PARAMETER = "--debug"
private const val FILTERS_PARAMETER = "--filters"
private const val INPUT_PARAMETER = "--input"
private const val CONFIG_PARAMETER = "--config"
private const val CLASSPATH_PARAMETER = "--classpath"
private const val BASELINE_PARAMETER = "--baseline"
private const val PARALLEL_PARAMETER = "--parallel"
private const val DISABLE_DEFAULT_RULESETS_PARAMETER = "--disable-default-rulesets"
private const val PLUGINS_PARAMETER = "--plugins"
private const val REPORT_PARAMETER = "--report"
private const val GENERATE_CONFIG_PARAMETER = "--generate-config"
private const val CREATE_BASELINE_PARAMETER = "--create-baseline"

internal sealed class CliArgument {
	abstract fun toArgument(): List<String>
}

internal object CreateBaselineArgument : CliArgument() {
	override fun toArgument() = listOf(CREATE_BASELINE_PARAMETER)
}

internal data class ClasspathArgument(val fileCollection: FileCollection) : CliArgument() {
	override fun toArgument(): List<String> {
		if (fileCollection.isEmpty()) return emptyList()
		return listOf(CLASSPATH_PARAMETER, fileCollection.joinToString(File.pathSeparator) { it.absolutePath })
	}
}

internal object GenerateConfigArgument : CliArgument() {
	override fun toArgument() = listOf(GENERATE_CONFIG_PARAMETER)
}

internal data class InputArgument(val fileCollection: FileCollection) : CliArgument() {
	override fun toArgument() = listOf(INPUT_PARAMETER, fileCollection.joinToString(",") { it.absolutePath })
}

internal data class FiltersArgument(val filters: String?) : CliArgument() {
	override fun toArgument() = filters?.let { listOf(FILTERS_PARAMETER, it) } ?: emptyList()
}

internal data class PluginsArgument(val plugins: String?) : CliArgument() {
	override fun toArgument() = plugins?.let { listOf(PLUGINS_PARAMETER, it) } ?: emptyList()
}

internal data class BaselineArgument(val baseline: RegularFile?) : CliArgument() {
	override fun toArgument() = baseline?.let { listOf(BASELINE_PARAMETER, it.asFile.absolutePath) } ?: emptyList()
}

internal data class XmlReportArgument(val file: RegularFile?) : CliArgument() {
	override fun toArgument() = file?.let { listOf(REPORT_PARAMETER, "xml:${it.asFile.absoluteFile}") } ?: emptyList()
}

internal data class HtmlReportArgument(val file: RegularFile?) : CliArgument() {
	override fun toArgument() = file?.let { listOf(REPORT_PARAMETER, "html:${it.asFile.absolutePath}") } ?: emptyList()
}

internal data class ConfigArgument(val config: FileCollection) : CliArgument() {
	override fun toArgument() = if (config.isEmpty)
		emptyList()
	else
		listOf(CONFIG_PARAMETER, config.joinToString(",") { it.absolutePath })
}

internal data class DebugArgument(val value: Boolean) : CliArgument() {
	override fun toArgument() = if (value) listOf(DEBUG_PARAMETER) else emptyList()
}

internal data class ParallelArgument(val value: Boolean) : CliArgument() {
	override fun toArgument() = if (value) listOf(PARALLEL_PARAMETER) else emptyList()
}

internal data class DisableDefaultRulesetArgument(val value: Boolean) : CliArgument() {
	override fun toArgument() = if (value) listOf(DISABLE_DEFAULT_RULESETS_PARAMETER) else emptyList()
}

