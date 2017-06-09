package io.gitlab.arturbosch.detekt

import org.gradle.api.Action

/**
 * @author Artur Bosch
 */
@Suppress("LongParameterList")
open class DetektExtension(open var version: String = "1.0.0.M11",
						   open var input: String? = null,
						   open var config: String? = null,
						   open var configResource: String? = null,
						   open var generateConfig: Boolean = false,
						   open var filters: String? = null,
						   open var rulesets: String? = null,
						   open var output: String? = null,
						   open var baseline: String? = null,
						   open var parallel: Boolean = false,
						   open var format: Boolean = false,
						   open var useTabs: Boolean = false,
						   open var disableDefaultRuleSets: Boolean = false,
						   open var debug: Boolean = false,
						   open var ideaExtension: IdeaExtension = IdeaExtension()) {

	val detektArgs: MutableList<String> by lazy { convertToArguments() }
	val ideaFormatArgs get() = ideaExtension.formatArgs(this)
	val ideaInspectArgs get() = ideaExtension.inspectArgs(this)

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}

	private fun convertToArguments(): MutableList<String> {
		return mutableListOf<String>().apply {
			input?.let { add("--project"); add(it) }
			config?.let { add("--config"); add(it) }
			configResource?.let { add("--config-resource"); add(it) }
			if (generateConfig) add("--generate-config")
			filters?.let { add("--filters"); add(it) }
			rulesets?.let { add("--rules"); add(it) }
			output?.let { add("--output"); add(it) }
			baseline?.let { add("--baseline"); add(it) }
			if (parallel) add("--parallel")
			if (format) add("--format")
			if (useTabs) add("--useTabs")
			if (disableDefaultRuleSets) add("--disable-default-rulesets")
			if (debug) println("detekt version: $version: " + this)
		}
	}

}
