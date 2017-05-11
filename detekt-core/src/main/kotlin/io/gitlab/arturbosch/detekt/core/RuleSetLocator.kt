package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class RuleSetLocator(val excludeDefaultRuleSets: Boolean,
					 val ruleSets: List<Path>) {
	init {
		ruleSets.forEach {
			require(Files.exists(it) && it.toString().endsWith("jar")) {
				"Given ruleset $it does not exist or has no jar ending!"
			}
		}
	}

	companion object {
		fun instance(settings: ProcessingSettings) = with(settings) {
			RuleSetLocator(excludeDefaultRuleSets, ruleSets)
		}
	}

	fun loadProviders(): List<RuleSetProvider> {
		val urls = ruleSets.map { it.toUri().toURL() }.toTypedArray()
		val detektLoader = URLClassLoader(urls, javaClass.classLoader)
		return ServiceLoader.load(RuleSetProvider::class.java, detektLoader).asIterable()
				.map { it.nullIfDefaultAndExcluded() }
				.filterNotNull()
				.toList()
	}

	private fun RuleSetProvider.nullIfDefaultAndExcluded() = if (excludeDefaultRuleSets && provided()) null else this

	private fun RuleSetProvider.provided() = ruleSetId in defaultRuleSetIds

	private val defaultRuleSetIds = listOf("code-smell", "comments", "complexity", "empty",
			"exceptions", "potential-bugs", "style")
}
