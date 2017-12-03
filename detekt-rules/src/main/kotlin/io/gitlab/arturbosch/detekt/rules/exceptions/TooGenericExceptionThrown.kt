package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @configuration exceptions - exceptions which are too generic and should not be thrown
 * (default: - Error
 * 			 - Exception
 * 			 - NullPointerException
 *			 - Throwable
 * 			 - RuntimeException)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class TooGenericExceptionThrown(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"Thrown exception is too generic. " +
					"Prefer throwing project specific exceptions to handle error cases.")

	private val exceptions: Set<String> = valueOrDefault(THROWN_EXCEPTIONS_PROPERTY, thrownExceptionDefaults).toHashSet()

	override fun visitThrowExpression(expression: KtThrowExpression) {
		expression.thrownExpression?.text?.substringBefore("(")?.let {
			if (it in exceptions) report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
		super.visitThrowExpression(expression)
	}

	companion object {
		const val THROWN_EXCEPTIONS_PROPERTY = "exceptions"
	}
}

val thrownExceptionDefaults = listOf(
		"Error",
		"Exception",
		"NullPointerException",
		"Throwable",
		"RuntimeException"
)
