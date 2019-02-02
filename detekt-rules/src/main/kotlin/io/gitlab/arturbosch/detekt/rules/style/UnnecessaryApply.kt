package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.parentsOfTypeUntil
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtVariableDeclaration
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

		if (expression.isApplyExpr() && expression.isViolation()) {
			report(CodeSmell(
					issue, Entity.from(expression),
					"apply expression can be omitted"
			))
		}
	}

	private fun KtCallExpression.isViolation() =
			!isInsideFunctionCall() &&
					hasOnlyOneStatement() &&
					!isInsideAssignment() &&
					!isReturnedFromFunction()
}

private fun KtCallExpression.hasOnlyOneStatement(): Boolean {
	val lambdaBody = firstLambdaArg?.bodyExpression
	if (lambdaBody.hasOnlyOneStatement()) {
		return lambdaBody?.statements?.firstOrNull()
				?.safeAs<KtBinaryExpression>()
				?.operationToken != KtTokens.EQ
	}
	return false
}

private fun KtCallExpression.isReturnedFromFunction() = parent is KtNamedFunction

private fun KtCallExpression.isInsideAssignment() =
		parentsOfTypeUntil<KtVariableDeclaration, KtBlockExpression>().firstOrNull() != null

private fun KtCallExpression.isInsideFunctionCall(): Boolean =
		parent.safeAs<KtDotQualifiedExpression>()
				?.getParentOfType<KtValueArgument>(
						true,
						KtCallExpression::class.java, KtDeclaration::class.java
				) != null

private const val APPLY_LITERAL = "apply"

private fun KtCallExpression.isApplyExpr() = calleeExpression?.textMatches(APPLY_LITERAL) == true

private val KtCallExpression.firstLambdaArg
	get() = lambdaArguments.firstOrNull()
			?.getLambdaExpression()

private fun KtBlockExpression?.hasOnlyOneStatement() = this?.children?.size == 1
