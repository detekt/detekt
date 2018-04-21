package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.DetektProgressListener
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createPathFilters
import io.gitlab.arturbosch.detekt.cli.createPlugins
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.RuleSetLocator

/**
 * @author Artur Bosch
 */
class SingleRuleRunner(private val arguments: CliArgs) : Executable {

	override fun execute() {
		val (ruleSet, rule) = arguments.runRule?.split(":")
				?: throw IllegalStateException("Unexpected empty 'runRule' argument.")

		val settings = ProcessingSettings(
				arguments.inputPath,
				arguments.loadConfiguration(),
				arguments.createPathFilters(),
				arguments.parallel,
				arguments.disableDefaultRuleSets,
				arguments.createPlugins())

		val ruleToRun = RuleSetLocator(settings).load()
				.find { it.ruleSetId == ruleSet }
				?.buildRuleset(Config.empty)
				?.rules
				?.find { it.id == rule }
				?: throw IllegalArgumentException("There was no rule '$rule' in rule set '$ruleSet'.")

		val provider = FakeRuleSetProvider("$ruleSet-$rule", ruleToRun)
		val detektion = DetektFacade.create(
				settings,
				listOf(provider),
				listOf(DetektProgressListener())
		).run()
		OutputFacade(arguments, detektion, settings).run()
	}
}

private class FakeRuleSetProvider(
		runPattern: String,
		private val rule: BaseRule) : RuleSetProvider {

	override val ruleSetId: String = runPattern

	override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(rule))
}
