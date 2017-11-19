package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */
@Suppress("LongParameterList", "ComplexMethod")
open class ProfileExtension(val name: String,
							open var input: String? = null,
							open var config: String? = null,
							open var configResource: String? = null,
							open var filters: String? = null,
							open var ruleSets: String? = null,
							open var output: String? = null,
							open var outputName: String? = null,
							open var baseline: String? = null,
							open var parallel: Boolean = false,
							open var disableDefaultRuleSets: Boolean = false,
							open var plugins: String? = null) {

	fun arguments(debug: Boolean = false) = mutableMapOf<String, String>().apply {
		input?.let { put(INPUT_PARAMETER, it) }
		config?.let { put(CONFIG_PARAMETER, it) }
		configResource?.let { put(CONFIG_RESOURCE_PARAMETER, it) }
		filters?.let { put(FILTERS_PARAMETER, it) }
		ruleSets?.let { put(RULES_PARAMETER, it) }
		output?.let { put(OUTPUT_PARAMETER, it) }
		outputName?.let { put(OUTPUT_NAME_PARAMETER, it) }
		baseline?.let { put(BASELINE_PARAMETER, it) }
		plugins?.let { put(PLUGINS_PARAMETER, it) }
		if (parallel) put(PARALLEL_PARAMETER, DEFAULT_TRUE)
		if (disableDefaultRuleSets) put(DISABLE_DEFAULT_RULESETS_PARAMETER, DEFAULT_TRUE)
		if (debug) put(DEBUG_PARAMETER, DEFAULT_TRUE)
	}

	override fun toString(): String = "ProfileExtension(name='$name', input=$input, config=$config, " +
			"configResource=$configResource, filters=$filters, ruleSets=$ruleSets, output=$output, " +
			"outputName=$outputName, baseline=$baseline, parallel=$parallel, " +
			"disableDefaultRuleSets=$disableDefaultRuleSets)"

}
