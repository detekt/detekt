package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.cli.Executable
import io.gitlab.arturbosch.detekt.cli.Main
import io.gitlab.arturbosch.detekt.core.KtCompiler

/**
 * @author Artur Bosch
 */
class Debugger(val main: Main) : Executable {

	override fun execute() {
		val ktFile = KtCompiler(main.project).compile(main.project)
		DebugRuleSetProvider.buildRuleset(Config.empty)?.rules?.forEach {
			println("Rule: ${it.id}\n")
			it.visit(ktFile)
			println()
		}
	}
}
