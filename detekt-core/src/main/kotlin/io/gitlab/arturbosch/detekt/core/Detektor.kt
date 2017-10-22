package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.FindingsForFile
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.toMergedMap
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class Detektor(private val settings: ProcessingSettings,
			   private val providers: List<RuleSetProvider>,
			   private val processors: List<FileProcessListener> = emptyList()) {

	private val config: Config = settings.config
	private val modifier: KtFileModifier = KtFileModifier(settings.project)
	private val testPattern: TestPattern = settings.loadTestPattern()
	private val notifications: MutableList<Notification> = mutableListOf()

	fun run(compiler: KtTreeCompiler = KtTreeCompiler.instance(settings)): Detektion = run(compiler.compile())

	fun run(ktFiles: List<KtFile>): Detektion = withExecutor {

		processors.forEach { it.onStart(ktFiles) }
		val futures = ktFiles.map { file ->
			runAsync {
				processors.forEach { it.onProcess(file) }
				file.analyze().apply {
					processors.forEach { it.onProcessComplete(file, FindingsForFile(this)) }
				}
			}
		}
		val findings = awaitAll(futures).flatMap { it }.toMergedMap()

		if (config.valueOrDefault("autoCorrect", false)) {
			modifier.saveModifiedFiles(ktFiles) {
				notifications.add(it)
			}
		}

		DetektResult(findings.toSortedMap(), notifications).apply {
			processors.forEach { it.onFinish(ktFiles, this) }
		}
	}

	private fun KtFile.analyze(): List<Pair<String, List<Finding>>> {
		var ruleSets = providers.mapNotNull { it.buildRuleset(config) }
				.sortedBy { it.id }
				.distinctBy { it.id }

		return if (testPattern.isTestSource(this)) {
			ruleSets = ruleSets.filterNot { testPattern.matchesRuleSet(it.id) }
			ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this, testPattern.excludingRules) }
		} else {
			ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this) }
		}
	}
}
