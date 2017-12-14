package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.toMergedMap
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class Detektor(settings: ProcessingSettings,
			   private val providers: List<RuleSetProvider>,
			   private val processors: List<FileProcessListener> = emptyList()) {

	private val config: Config = settings.config
	private val testPattern: TestPattern = settings.loadTestPattern()

	fun run(ktFiles: List<KtFile>): Map<String, List<Finding>> = withExecutor {

		val futures = ktFiles.map { file ->
			runAsync {
				processors.forEach { it.onProcess(file) }
				file.analyze().apply {
					processors.forEach { it.onProcessComplete(file, this) }
				}
			}
		}

		val result = HashMap<String, List<Finding>>()
		for (map in awaitAll(futures)) {
			result.mergeSmells(map)
		}

		result
	}

	private fun KtFile.analyze(): Map<String, List<Finding>> {
		var ruleSets = providers.mapNotNull { it.buildRuleset(config) }
				.sortedBy { it.id }
				.distinctBy { it.id }

		return if (testPattern.isTestSource(this)) {
			ruleSets = ruleSets.filterNot { testPattern.matchesRuleSet(it.id) }
			ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this, testPattern.excludingRules) }
		} else {
			ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this) }
		}.toMergedMap()
	}
}
