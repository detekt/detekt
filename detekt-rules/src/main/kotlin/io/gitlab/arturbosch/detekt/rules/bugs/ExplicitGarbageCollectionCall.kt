package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * @author Artur Bosch
 */
class ExplicitGarbageCollectionCall(config: Config) : Rule("ExplicitGarbageCollectionCall", config) {

	override fun visitCallExpression(context: Context, expression: KtCallExpression) {
		expression.getCallNameExpression()?.let {
			matchesGCCall(context, expression, it)
		}
	}

	private fun matchesGCCall(context: Context, expression: KtCallExpression, it: KtSimpleNameExpression) {
		if (it.textMatches("gc") || it.textMatches("runFinalization")) {
			it.getReceiverExpression()?.let {
				when (it.text) {
					"System", "Runtime.getRuntime()" -> context.report(CodeSmell(ISSUE, Entity.Companion.from(expression)))
				}
			}
		}
	}

	companion object {
		val ISSUE = Issue("ExplicitGarbageCollectionCall", Issue.Severity.Maintainability)
	}
}