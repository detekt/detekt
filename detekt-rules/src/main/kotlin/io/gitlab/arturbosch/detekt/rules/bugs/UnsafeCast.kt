package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtPsiUtil

/**
 * @author Ivan Balaksha
 */
class UnsafeCast(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("UnsafeCast",
			Severity.Defect,
			"Cast operator throws an exception if the cast is not possible.")

	override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
		if (KtPsiUtil.isUnsafeCast(expression)) {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
	}
}
