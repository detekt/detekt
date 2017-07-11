package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */

val path: Path = Paths.get(resource("/cases"))

class TestProvider(override val ruleSetId: String = "Test") : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("Test", listOf())
	}
}

class TestProvider2(override val ruleSetId: String = "Test2") : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("Test", listOf())
	}
}
