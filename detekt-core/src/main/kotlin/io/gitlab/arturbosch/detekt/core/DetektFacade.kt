package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
object DetektFacade {

	fun instance(settings: ProcessingSettings): Detektor {
		val providers = RuleSetLocator(settings).load()
		val processors = FileProcessorLocator(settings).load()
		return instance(settings, providers, processors)
	}

	fun instance(settings: ProcessingSettings, vararg providers: RuleSetProvider): Detektor {
		return instance(settings, providers.toList(), emptyList())
	}

	fun instance(settings: ProcessingSettings, vararg processors: FileProcessListener): Detektor {
		return instance(settings, emptyList(), processors.toList())
	}

	fun instance(settings: ProcessingSettings,
				 providers: List<RuleSetProvider>,
				 processors: List<FileProcessListener>): Detektor {
		val compiler = KtTreeCompiler.instance(settings)
		return Detektor(settings, compiler, providers, processors)
	}
}
