package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The empty-blocks ruleset contains rules that will report empty blocks of code
 * which should be avoided.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyCodeProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("empty-blocks")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::EmptyCatchBlock,
            ::EmptyClassBlock,
            ::EmptyDefaultConstructor,
            ::EmptyDoWhileBlock,
            ::EmptyElseBlock,
            ::EmptyFinallyBlock,
            ::EmptyForBlock,
            ::EmptyFunctionBlock,
            ::EmptyIfBlock,
            ::EmptyInitBlock,
            ::EmptyKotlinFile,
            ::EmptySecondaryConstructor,
            ::EmptyTryBlock,
            ::EmptyWhenBlock,
            ::EmptyWhileBlock,
        )
    )
}
