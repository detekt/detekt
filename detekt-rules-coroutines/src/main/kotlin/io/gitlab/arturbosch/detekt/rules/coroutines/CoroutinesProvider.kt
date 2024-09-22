package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * The coroutines rule set analyzes code for potential coroutines problems.
 */
@ActiveByDefault(since = "1.4.0")
class CoroutinesProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("coroutines")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::CoroutineLaunchedInTestWithoutRunTest,
            ::GlobalCoroutineUsage,
            ::InjectDispatcher,
            ::RedundantSuspendModifier,
            ::SleepInsteadOfDelay,
            ::SuspendFunWithFlowReturnType,
            ::SuspendFunWithCoroutineScopeReceiver,
            ::SuspendFunSwallowedCancellation,
            ::SuspendFunInFinallySection
        )
    )
}
