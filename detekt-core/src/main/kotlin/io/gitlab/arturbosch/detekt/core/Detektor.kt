package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.toMergedMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class Detektor(settings: ProcessingSettings,
			   private val providers: List<RuleSetProvider>,
			   private val processors: List<FileProcessListener> = emptyList()) {

	private val config: Config = settings.config
	private val testPattern: TestPattern = settings.loadTestPattern()
	private val logger = settings.errorPrinter

	@Suppress("detekt.TooGenericExceptionCaught")
	fun run(ktFiles: List<KtFile>): Map<RuleSetId, List<Finding>> {

		return runBlocking {
			ktFiles.map { file ->
				file to processFileAsync(file)
			}.map { (file, deferred) ->
				try {
					deferred.await()
				} catch (e: Exception) {
					logger.println("\n\nAnalyzing '${file.absolutePath()}' led to an exception.\n" +
							"Running detekt '${whichDetekt()}' on Java '${whichJava()}' on OS '${whichOS()}'.\n" +
							"Please create an issue and report this exception.")
					e.printStacktraceRecursively(logger)
					HashMap<RuleSetId, List<Finding>>()
				}
			}.reduce { acc, map ->
				acc.mergeSmells(map)
			}
		}
	}

	private fun CoroutineScope.processFileAsync(file: KtFile): Deferred<MutableMap<RuleSetId, List<Finding>>> {
		return async {
			processors.forEach { it.onProcess(file) }
			file.analyze().apply {
				processors.forEach { it.onProcessComplete(file, this) }
			}
		}
	}

	private fun KtFile.analyze(): MutableMap<RuleSetId, List<Finding>> {
		var ruleSets = providers.asSequence()
				.mapNotNull { it.buildRuleset(config) }
				.sortedBy { it.id }
				.distinctBy { it.id }
				.toList()

		return if (testPattern.isTestSource(this)) {
			ruleSets = ruleSets.filterNot { testPattern.matchesRuleSet(it.id) }
			ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this, testPattern.excludingRules) }
		} else {
			ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this) }
		}.toMergedMap().toMutableMap()
	}
}
