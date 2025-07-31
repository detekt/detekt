package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.internal.DefaultRuleSetProvider

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
