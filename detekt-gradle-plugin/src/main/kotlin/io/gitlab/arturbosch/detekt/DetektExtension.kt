package io.gitlab.arturbosch.detekt

/**
 * @author Artur Bosch
 */
@Suppress("LongParameterList")
open class DetektExtension(var version: String = "1.0.0.M10.3",
						   var input: String? = null,
						   var config: String? = null,
						   var configResource: String? = null,
						   var generateConfig: Boolean = false,
						   var filters: String? = null,
						   var rulesets: String? = null,
						   var report: String? = null,
						   var output: Boolean = false,
						   var baseline: Boolean = false,
						   var parallel: Boolean = false,
						   var format: Boolean = false,
						   var useTabs: Boolean = false,
						   var disableDefaultRuleSets: Boolean = false,
						   var debug: Boolean = false) {

	fun convertToArguments(): MutableList<String> {
		return mutableListOf<String>().apply {
			input?.let { add("--project"); add(it) }
			config?.let { add("--config"); add(it) }
			configResource?.let { add("--config-resource"); add(it) }
			if (generateConfig) add("--generate-config")
			filters?.let { add("--filters"); add(it) }
			rulesets?.let { add("--rules"); add(it) }
			report?.let { add("--report"); add(it) }
			if (output) add("--output")
			if (baseline) add("--baseline")
			if (parallel) add("--parallel")
			if (format) add("--format")
			if (useTabs) add("--useTabs")
			if (disableDefaultRuleSets) add("--disable-default-rulesets")
		}
	}

}
