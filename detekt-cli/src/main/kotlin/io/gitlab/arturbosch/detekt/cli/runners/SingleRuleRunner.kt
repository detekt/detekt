package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.DetektProgressListener
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createFilters
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
        val (ruleSet, rule: RuleId) = arguments.runRule?.split(":")
            ?: throw IllegalStateException("Unexpected empty 'runRule' argument.")

        val settings = with(arguments) {
            ProcessingSettings(
                inputPaths = inputPaths,
                config = loadConfiguration(),
                pathFilters = createFilters(),
                parallelCompilation = parallel,
                autoCorrect = autoCorrect,
                excludeDefaultRuleSets = disableDefaultRuleSets,
                pluginPaths = createPlugins())
        }

        val realProvider = RuleSetLocator(settings).load()
            .find { it.ruleSetId == ruleSet }
            ?: throw IllegalArgumentException("There was no rule set with id '$ruleSet'.")

        val provider = RuleProducingProvider(rule, realProvider)
        val detektion = DetektFacade.create(
            settings,
            listOf(provider),
            listOf(DetektProgressListener())
        ).run()
        OutputFacade(arguments, detektion, settings).run()
    }
}

private class RuleProducingProvider(
    private val ruleId: RuleId,
    private val provider: RuleSetProvider
) : RuleSetProvider {

    override val ruleSetId: String = provider.ruleSetId + "-" + ruleId

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(produceRule())
    )

    private fun produceRule(): BaseRule = (provider.buildRuleset(Config.empty)
        ?.rules
        ?.find { it.ruleId == ruleId }
        ?: throw IllegalArgumentException("There was no rule '$ruleId' in rule set '${provider.ruleSetId}'."))
}
