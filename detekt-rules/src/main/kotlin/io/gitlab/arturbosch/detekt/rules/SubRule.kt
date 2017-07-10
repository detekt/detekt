package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule

abstract class SubRule<in Element>(config: Config) : Rule(config) {
	abstract fun apply(element: Element)
}