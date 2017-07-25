package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class Detektor(settings: ProcessingSettings,
			   val compiler: KtTreeCompiler,
			   val providers: List<RuleSetProvider>,
			   val processors: List<FileProcessListener> = emptyList()) {

	private val config: Config = settings.config
	private val notifications: MutableList<Notification> = mutableListOf()

	fun run(): Detektion {
		val ktFiles = compiler.compile()
		return withExecutor {

			processors.forEach { it.onStart(ktFiles) }
			val futures = ktFiles.map { file ->
				runAsync {
					file.analyze().apply {
						processors.forEach { it.onProcess(file) }
					}
				}
			}
			val findings = awaitAll(futures).flatMap { it }.toMergedMap()

			if (config.valueOrDefault("autoCorrect", false)) {
				compiler.saveModifiedFiles(ktFiles) {
					notifications.add(it)
				}
			}

			DetektResult(findings.toSortedMap(), notifications).apply {
				processors.forEach { it.onFinish(ktFiles, this) }
			}
		}
	}

	private fun KtFile.analyze(): List<Pair<String, List<Finding>>> {
		return providers.map { it.buildRuleset(config) }
				.filterNotNull()
				.sortedBy { it.id }
				.distinctBy { it.id }
				.map { rule -> rule.id to rule.accept(this) }
	}
}
