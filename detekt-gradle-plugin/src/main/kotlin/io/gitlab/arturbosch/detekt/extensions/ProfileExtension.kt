package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */
@Suppress("LongParameterList")
open class ProfileExtension(val name: String,
							open var input: String? = null,
							open var config: String? = null,
							open var configResource: String? = null,
							open var filters: String? = null,
							open var ruleSets: String? = null,
							open var output: String? = null,
							open var outputFormat: String? = null,
							open var baseline: String? = null,
							open var parallel: Boolean = false,
							open var disableDefaultRuleSets: Boolean = false) {

	fun arguments(debug: Boolean = false): MutableMap<String, String> {
		return mutableMapOf<String, String>().apply {
			input?.let { put(PROJECT_PARAMETER, it) }
			config?.let { put(CONFIG_PARAMETER, it) }
			configResource?.let { put(CONFIG_RESOURCE_PARAMETER, it) }
			filters?.let { put(FILTERS_PARAMETER, it) }
			ruleSets?.let { put(RULES_PARAMETER, it) }
			output?.let { put(OUTPUT_PARAMETER, it) }
			outputFormat?.let { put(OUTPUT_FORMAT_PARAMETER, it) }
			baseline?.let { put(BASELINE_PARAMETER, it) }
			if (parallel) put(PARALLEL_PARAMETER, DEFAULT_TRUE)
			if (disableDefaultRuleSets) put(DISABLE_DEFAULT_RULESETS_PARAMETER, DEFAULT_TRUE)
			if (debug) put("--debug", DEFAULT_TRUE)
		}
	}

	override fun toString(): String = this.reflectiveToString()

}