package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @author Artur Bosch
 */
class TooGenericExceptionThrown(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"Thrown exception is too generic. " +
					"Prefer throwing project specific exceptions to handle error cases.")

	private val exceptions: Set<String> = valueOrDefault(THROWN_EXCEPTIONS_PROPERTY, THROWN_EXCEPTIONS).toHashSet()

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

val THROWN_EXCEPTIONS = listOf(
		"Error",
		"Exception",
		"NullPointerException",
		"Throwable",
		"RuntimeException"
)
