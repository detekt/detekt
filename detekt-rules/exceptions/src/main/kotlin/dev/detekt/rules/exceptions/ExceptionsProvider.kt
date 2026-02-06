package dev.detekt.rules.exceptions

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * Rules in this rule set report issues related to how code throws and handles Exceptions.
 */
@ActiveByDefault(since = "1.0.0")
class ExceptionsProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("exceptions")

    override fun instance(): RuleSet =
        RuleSet(
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
                ::ObjectExtendsThrowable,
                ::ErrorUsageWithThrowable,
            )
        )
}
