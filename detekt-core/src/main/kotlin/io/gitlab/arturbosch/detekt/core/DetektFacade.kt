package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * @author Artur Bosch
 */
object DetektFacade {

	fun instance(settings: ProcessingSettings): Detektor {
		val locator = RuleSetLocator.instance(settings)
		val providers = locator.loadProviders()
		return instance(settings, providers)
	}

	fun instance(settings: ProcessingSettings, vararg providers: RuleSetProvider): Detektor {
		return instance(settings, providers.toList())
	}

	fun instance(settings: ProcessingSettings, providers: List<RuleSetProvider>): Detektor {
		val compiler = KtTreeCompiler.instance(settings)
		return Detektor(settings, compiler, providers)
	}
}
