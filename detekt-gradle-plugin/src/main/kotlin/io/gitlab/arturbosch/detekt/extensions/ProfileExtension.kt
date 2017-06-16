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

	fun arguments(): MutableMap<String, String> {
		return mutableMapOf<String, String>().apply {
			input?.let { put("--project", it) }
			config?.let { put("--config", it) }
			configResource?.let { put("--config-resource", it) }
			filters?.let { put("--filters", it) }
			ruleSets?.let { put("--rules", it) }
			output?.let { put("--output", it) }
			outputFormat?.let { put("--output-format", it) }
			baseline?.let { put("--baseline", it) }
			if (parallel) put("--parallel", "true")
			if (disableDefaultRuleSets) put("--disable-default-rulesets", "true")
		}
	}

	override fun toString(): String = this.reflectiveToString()


}