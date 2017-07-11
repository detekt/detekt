package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * @author Artur Bosch
 */
class ExplicitGarbageCollectionCall(config: Config) : Rule(config) {

	override val issue = Issue("ExplicitGarbageCollectionCall",
			Severity.Defect,
			"Don't try to be smarter than the JVM. Your code should work independently if the garbage " +
					"collector is disabled or not. If you face memory issues, " +
					"try tuning the JVM options instead of relying on code itself.")

	override fun visitCallExpression(expression: KtCallExpression) {
		expression.getCallNameExpression()?.let {
			matchesGCCall(expression, it)
		}
	}

	private fun matchesGCCall(expression: KtCallExpression, it: KtSimpleNameExpression) {
		if (it.textMatches("gc") || it.textMatches("runFinalization")) {
			it.getReceiverExpression()?.let {
				when (it.text) {
					"System", "Runtime.getRuntime()" -> report(CodeSmell(issue, Entity.Companion.from(expression)))
				}
			}
		}
	}
}
