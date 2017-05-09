package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class Detektor(settings: ProcessingSettings,
			   val compiler: KtTreeCompiler,
			   val locator: RuleSetLocator) {

	private val config: Config = settings.config
	private val changeListeners: List<FileProcessListener> = settings.changeListeners
	private val notifications: MutableList<Notification> = mutableListOf()

	fun run(): Detektion {
		val ktFiles = compiler.compile()
		val providers = locator.loadProviders()
		return withExecutor {

			changeListeners.forEach { it.onStart(ktFiles) }
			val futures = ktFiles.map { file ->
				runAsync {
					file.detect(providers).apply {
						changeListeners.forEach { it.onProcess(file) }
					}
				}
			}
			val findings = awaitAll(futures).flatMap { it }.toMergedMap()

			if (config.valueOrDefault("autoCorrect") { false }) {
				compiler.saveModifiedFiles(ktFiles) {
					notifications.add(it)
				}
			}

			DetektResult(findings.toSortedMap(), notifications).apply {
				changeListeners.forEach {
					it.onFinish(ktFiles, this)
				}
			}
		}
	}

	private fun KtFile.detect(providers: List<RuleSetProvider>): List<Pair<String, List<Finding>>> {
		return providers.map { it.buildRuleset(config) }
				.filterNotNull()
				.sortedBy { it.id }
				.distinctBy { it.id }
				.map { rule -> rule.id to rule.accept(this) }
	}
}
