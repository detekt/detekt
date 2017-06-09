package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * @author Artur Bosch
 */
class ExplicitGarbageCollectionCall(config: Config) : Rule("ExplicitGarbageCollectionCall", Severity.Maintainability, config) {

	override fun visitCallExpression(expression: KtCallExpression) {
		expression.getCallNameExpression()?.let {
			matchesGCCall(expression, it)
		}
	}

	private fun matchesGCCall(expression: KtCallExpression, it: KtSimpleNameExpression) {
		if (it.textMatches("gc") || it.textMatches("runFinalization")) {
			it.getReceiverExpression()?.let {
				when (it.text) {
					"System", "Runtime.getRuntime()" -> addFindings(CodeSmell(id, severity, Entity.Companion.from(expression)))
				}
			}
		}
	}
}