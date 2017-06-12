package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class Detektor(settings: ProcessingSettings,
			   val compiler: KtTreeCompiler,
			   val providers: List<RuleSetProvider>) {

	private val config: Config = settings.config
	private val changeListeners: List<FileProcessListener> = settings.changeListeners
	private val notifications: MutableList<Notification> = mutableListOf()

	fun run(): Detektion {
		val ktFiles = compiler.compile()
		return withExecutor {

			changeListeners.forEach { it.onStart(ktFiles) }
			val futures = ktFiles.map { file ->
				runAsync {
					val context = Context()
					file.analyze(context).apply {
						changeListeners.forEach { it.onProcess(context, file) }
					}
				}
			}
			val findings: Map<Issue, List<Finding>> = awaitAll(futures)
					.flatMap { it.findings }
					.groupBy { it.issue }

			if (config.valueOrDefault("autoCorrect", false)) {
				compiler.saveModifiedFiles(ktFiles) {
					notifications.add(it)
				}
			}

			DetektResult(findings, notifications).apply {
				changeListeners.forEach {
					it.onFinish(ktFiles, this)
				}
			}
		}
	}

	private fun KtFile.analyze(context: Context): Context {
		providers.map { it.buildRuleset(config) }
				.filterNotNull()
				.sortedBy { it.id }
				.distinctBy { it.id }
				.forEach { rule -> rule.accept(context, this) }
		return context
	}
}
