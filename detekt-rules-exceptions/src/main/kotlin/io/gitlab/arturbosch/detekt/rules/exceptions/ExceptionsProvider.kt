package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * Rules in this rule set report issues related to how code throws and handles Exceptions.
 */
@ActiveByDefault(since = "1.0.0")
class ExceptionsProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "exceptions"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            TooGenericExceptionCaught(config),
            ExceptionRaisedInUnexpectedLocation(config),
            TooGenericExceptionThrown(config),
            NotImplementedDeclaration(config),
            PrintStackTrace(config),
            InstanceOfCheckForException(config),
            ThrowingExceptionsWithoutMessageOrCause(config),
            ReturnFromFinally(config),
            ThrowingExceptionFromFinally(config),
            ThrowingExceptionInMain(config),
            RethrowCaughtException(config),
            ThrowingNewInstanceOfSameException(config),
            SwallowedException(config),
            ObjectExtendsThrowable(config)
        )
    )
}
