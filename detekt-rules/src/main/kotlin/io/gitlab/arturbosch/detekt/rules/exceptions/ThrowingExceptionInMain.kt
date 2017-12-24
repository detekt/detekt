package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * This rule reports all exceptions that are thrown in a `main` method.
 *
 * <noncompliant>
 * fun main(args: Array<String>) {
 *     throw new IOException()
 * }
 * </noncompliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class ThrowingExceptionInMain(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ThrowingExceptionInMain", Severity.CodeSmell,
			"The main method should not throw an exception.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (isMainFunction(function) && hasArgsParameter(function.valueParameters) && containsThrowExpression(function)) {
			report(CodeSmell(issue, Entity.from(function), message = ""))
		}
	}

	private fun isMainFunction(function: KtNamedFunction): Boolean {
		return function.name == "main" && function.isPublicNotOverridden() && function.isTopLevel
	}

	private fun hasArgsParameter(parameters: List<KtParameter>): Boolean {
		return parameters.size == 1 && isStringArrayParameter(parameters.first().typeReference)
	}

	private fun isStringArrayParameter(typeReference: KtTypeReference?): Boolean {
		return typeReference?.text?.replace("\\s+", "") == "Array<String>"
	}

	private fun containsThrowExpression(function: KtNamedFunction): Boolean {
		return function.bodyExpression?.collectByType<KtThrowExpression>()?.any() == true
	}
}
