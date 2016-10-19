package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */

val path: Path = Paths.get(KtTreeCompilerSpec::class.java.getResource("/cases").path)

class TestProvider : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("Test", listOf())
	}
}

class TestProvider2 : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("Test", listOf())
	}
}