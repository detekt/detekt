package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElementOrCallableRef

/**
 * This rule detects the usage of the Pair constructor to create pairs of values.
 *
 * Using <value1> to <value2> is preferred.
 *
 * <noncompliant>
 * val pair = Pair(1, 2)
 * </noncompliant>
 *
 * <compliant>
 * val pair = 1 to 2
 * </compliant>
 *
 * @author jkaan
 */
class PreferToOverPairSyntax(config: Config = Config.empty) : Rule(config) {
	override val issue = Issue("PreferToOverPairSyntax", Severity.Style,
			"Pair was created using the Pair constructor, using the to syntax is preferred.",
			Debt.FIVE_MINS)

	override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
		val callReference = expression.getQualifiedElementOrCallableRef()
		if (expression.getReferencedName() == PAIR_CONSTRUCTOR_REFERENCE_NAME &&
				callReference is KtCallExpression) {
			val (firstArg, secondArg) = callReference.valueArguments.map { it.text }

			report(CodeSmell(issue, Entity.from(expression),
					message = "Pair is created by using the pair constructor. " +
							"This can replaced by `$firstArg to $secondArg`"))
		}

		super.visitSimpleNameExpression(expression)
	}

	companion object {
		const val PAIR_CONSTRUCTOR_REFERENCE_NAME = "Pair"
	}
}
