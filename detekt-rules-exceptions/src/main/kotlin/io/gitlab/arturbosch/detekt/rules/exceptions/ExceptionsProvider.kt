package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * Rules in this rule set report issues related to how code throws and handles Exceptions.
 */
@ActiveByDefault(since = "1.0.0")
class ExceptionsProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("exceptions")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::TooGenericExceptionCaught,
            ::ExceptionRaisedInUnexpectedLocation,
            ::TooGenericExceptionThrown,
            ::NotImplementedDeclaration,
            ::PrintStackTrace,
            ::InstanceOfCheckForException,
            ::ThrowingExceptionsWithoutMessageOrCause,
            ::ReturnFromFinally,
            ::ThrowingExceptionFromFinally,
            ::ThrowingExceptionInMain,
            ::RethrowCaughtException,
            ::ThrowingNewInstanceOfSameException,
            ::SwallowedException,
            ::ObjectExtendsThrowable
        )
    )
}
