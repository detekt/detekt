package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.exceptions.ExceptionRaisedInUnexpectedLocation
import io.gitlab.arturbosch.detekt.rules.exceptions.InstanceOfCheckForException
import io.gitlab.arturbosch.detekt.rules.exceptions.NotImplementedDeclaration
import io.gitlab.arturbosch.detekt.rules.exceptions.PrintStackTrace
import io.gitlab.arturbosch.detekt.rules.exceptions.RethrowCaughtException
import io.gitlab.arturbosch.detekt.rules.exceptions.ReturnFromFinally
import io.gitlab.arturbosch.detekt.rules.exceptions.SwallowedException
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowingExceptionFromFinally
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowingExceptionInMain
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowingExceptionsWithoutMessageOrCause
import io.gitlab.arturbosch.detekt.rules.exceptions.ThrowingNewInstanceOfSameException
import io.gitlab.arturbosch.detekt.rules.exceptions.TooGenericExceptionCaught
import io.gitlab.arturbosch.detekt.rules.exceptions.TooGenericExceptionThrown

/**
 * Rules in this rule set report issues related to how code throws and handles Exceptions.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class ExceptionsProvider : RuleSetProvider {

    override val ruleSetId: String = "exceptions"

    override fun instance(config: Config): RuleSet {
        return RuleSet(ruleSetId, listOf(
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
                SwallowedException(config)
        ))
    }
}
