package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The empty-blocks ruleset contains rules that will report empty blocks of code
 * which should be avoided.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyCodeProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "empty-blocks"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            EmptyCatchBlock(config),
            EmptyClassBlock(config),
            EmptyDefaultConstructor(config),
            EmptyDoWhileBlock(config),
            EmptyElseBlock(config),
            EmptyFinallyBlock(config),
            EmptyForBlock(config),
            EmptyFunctionBlock(config),
            EmptyIfBlock(config),
            EmptyInitBlock(config),
            EmptyKtFile(config),
            EmptySecondaryConstructor(config),
            EmptyTryBlock(config),
            EmptyWhenBlock(config),
            EmptyWhileBlock(config)
        )
    )
}
