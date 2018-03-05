package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtPsiUtil

/**
 * Reports casts which are unsafe. In case the cast is not possible it will throw an exception.
 *
 * <noncompliant>
 * fun foo(s: Any) {
 *     println(s as Int)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(s: Any) {
 *     println((s as? Int) ?: 0)
 * }
 * </compliant>
 *
 * @author Ivan Balaksha
 * @author Marvin Ramin
 */
class UnsafeCast(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("UnsafeCast",
			Severity.Defect,
			"Cast operator throws an exception if the cast is not possible.",
			Debt.TWENTY_MINS,
			aliases = setOf("UNCHECKED_CAST"))

	override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
		if (KtPsiUtil.isUnsafeCast(expression)) {
			report(CodeSmell(issue, Entity.from(expression),
					"${expression.left.text} cannot be safely cast to ${expression.right?.text ?: ""}."))
		}
	}
}
