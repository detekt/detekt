package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * Reports all `return` statements in `finally` blocks.
 * Using `return` statements in `finally` blocks can discard and hide exceptions that are thrown in the `try` block.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         throw MyException()
 *     } finally {
 *         return // prevents MyException from being propagated
 *     }
 * }
 * </noncompliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class ReturnFromFinally(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ReturnFromFinally", Severity.Defect,
			"Do not return within a finally statement. This can discard exceptions.")

	override fun visitFinallySection(finallySection: KtFinallySection) {
		val returnExpressions = finallySection.finalExpression.collectByType<KtReturnExpression>()
		val innerFunctions = finallySection.finalExpression.collectByType<KtNamedFunction>()
		returnExpressions.forEach {
			if (isNotInInnerFunction(it, innerFunctions)) {
				report(CodeSmell(issue, Entity.from(it), message = ""))
			}
		}
	}

	private fun isNotInInnerFunction(it: KtReturnExpression, childFunctions: List<KtNamedFunction>): Boolean {
		return !it.parents.any { childFunctions.contains(it) }
	}
}
