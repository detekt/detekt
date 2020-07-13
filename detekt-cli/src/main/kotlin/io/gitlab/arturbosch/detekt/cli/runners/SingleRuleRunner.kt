package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.DetektProgressListener
import io.gitlab.arturbosch.detekt.cli.createSettings
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.RuleSetLocator
import java.io.PrintStream

class SingleRuleRunner(
    private val arguments: CliArgs,
    private val outputPrinter: PrintStream,
    private val errorPrinter: PrintStream
) : Executable {

    override fun execute() {
        val (ruleSet, rule: RuleId) = checkNotNull(
            arguments.runRule?.split(":")
        ) { "Unexpected empty 'runRule' argument." }

        fun executeRule(settings: ProcessingSettings) {
            val realProvider = requireNotNull(
                RuleSetLocator(settings).load().find { it.ruleSetId == ruleSet }
            ) { "There was no rule set with id '$ruleSet'." }

            val provider = RuleProducingProvider(rule, realProvider)

            assertRuleExistsBeforeRunningItLater(provider, settings)

            DetektFacade(
                settings,
                listOf(provider),
                listOf(DetektProgressListener().apply { init(settings) })
            ).run()
        }

        arguments
            .createSettings(outputPrinter, errorPrinter)
            .use(::executeRule)
    }

    private fun assertRuleExistsBeforeRunningItLater(
        provider: RuleProducingProvider,
        settings: ProcessingSettings
    ) {
        assert(provider.instance(settings.config).rules.size == 1) { "Expected a single rule to be loaded." }
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

    private fun produceRule(): BaseRule =
        requireNotNull(
            provider.instance(Config.empty)
                .rules
                .find { it.ruleId == ruleId }
        ) { "There was no rule '$ruleId' in rule set '${provider.ruleSetId}'." }
}
