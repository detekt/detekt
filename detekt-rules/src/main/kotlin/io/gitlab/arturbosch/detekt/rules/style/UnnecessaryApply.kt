package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * `apply` expressions are used frequently, but sometimes their usage should be replaced with
 * an ordinary method/extension function call to reduce visual complexity
 *
 * <noncompliant>
 * config.apply { version = "1.2" } // can be replaced with `config.version = "1.2"`
 * config?.apply { environment = "test" } // can be replaced with `config?.environment = "test"`
 * </noncompliant>
 *
 * <compliant>
 * config.apply {
 *     version = "1.2"
 *     environment = "test"
 * }
 * </compliant>
 *
 * @author arjank
 */
class UnnecessaryApply(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"The `apply` usage is unnecessary", Debt.FIVE_MINS)

	override fun visitCallExpression(expression: KtCallExpression) {
		super.visitCallExpression(expression)

		if (!expression.isApplyExpr() ||
				expression.isInsideFunctionCall()) return

		val lambdaExpr = expression.firstLambdaArg
		val lambdaBody = lambdaExpr?.bodyExpression

		if (lambdaBody.hasOnlyOneStatement()) {
			report(CodeSmell(
					issue, Entity.from(expression),
					"apply expression can be omitted"
			))
		}
	}
}

private fun KtCallExpression.isInsideFunctionCall(): Boolean =
		(parent as? KtDotQualifiedExpression)
				?.getParentOfType<KtValueArgument>(
						true,
						KtCallExpression::class.java, KtDeclaration::class.java
				) != null

private const val APPLY_LITERAL = "apply"

private fun KtCallExpression.isApplyExpr() = calleeExpression?.textMatches(APPLY_LITERAL) == true

private val KtCallExpression.firstLambdaArg get() = lambdaArguments.firstOrNull()?.getLambdaExpression()

private fun KtBlockExpression?.hasOnlyOneStatement() = this?.children?.size == 1
