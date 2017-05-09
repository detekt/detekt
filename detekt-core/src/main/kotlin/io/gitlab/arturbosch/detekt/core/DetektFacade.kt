package io.gitlab.arturbosch.detekt.core

/**
 * @author Artur Bosch
 */
object DetektFacade {

	fun instance(settings: ProcessingSettings): Detektor {
		val compiler = KtTreeCompiler.instance(settings)
		val locator = RuleSetLocator.instance(settings)
		return Detektor(settings, compiler, locator)
	}

}