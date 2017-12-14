package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class DetektFacade(
		val detektor: Detektor,
		private val settings: ProcessingSettings,
		private val processors: List<FileProcessListener>) {

	private val saveSupported = settings.config.valueOrDefault("autoCorrect", false)
	private val pathsToAnalyze = settings.project
	private val compiler = KtTreeCompiler.instance(settings)

	fun run(): Detektion {
		val files = compiler.compile(pathsToAnalyze)
		return runOnFiles(files)
	}

	fun run(files: List<KtFile>): Detektion = runOnFiles(files)

	private fun runOnFiles(files: List<KtFile>): DetektResult {
		processors.forEach { it.onStart(files) }

		val findings = detektor.run(files)
		val detektion = DetektResult(findings.toSortedMap())
		if (saveSupported) {
			KtFileModifier(settings.project).saveModifiedFiles(files) {
				detektion.add(it)
			}
		}

		processors.forEach { it.onFinish(files, detektion) }
		return detektion
	}

	companion object {

		fun instance(settings: ProcessingSettings): DetektFacade {
			val providers = RuleSetLocator(settings).load()
			val processors = FileProcessorLocator(settings).load()
			return instance(settings, providers, processors)
		}

		fun instance(settings: ProcessingSettings, vararg providers: RuleSetProvider): DetektFacade {
			return instance(settings, providers.toList(), emptyList())
		}

		fun instance(settings: ProcessingSettings, vararg processors: FileProcessListener): DetektFacade {
			return instance(settings, emptyList(), processors.toList())
		}

		fun instance(settings: ProcessingSettings,
					 providers: List<RuleSetProvider>,
					 processors: List<FileProcessListener>): DetektFacade {
			return create(settings, providers, processors)
		}

		fun create(settings: ProcessingSettings,
				   providers: List<RuleSetProvider>,
				   processors: List<FileProcessListener>): DetektFacade {
			return DetektFacade(Detektor(settings, providers, processors), settings, processors)
		}
	}
}
