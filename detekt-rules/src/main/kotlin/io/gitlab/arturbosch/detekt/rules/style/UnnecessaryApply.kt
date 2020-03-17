package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression

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
 */
class UnnecessaryApply(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "The `apply` usage is unnecessary", Debt.FIVE_MINS)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.isApplyExpr() &&
                expression.hasOnlyOneMemberAccessStatement() &&
                expression.receiverIsUnused(bindingContext)) {
            report(CodeSmell(
                    issue, Entity.from(expression),
                    "apply expression can be omitted"
            ))
        }
    }
}

private fun KtCallExpression.receiverIsUnused(context: BindingContext): Boolean =
    (parent as? KtQualifiedExpression)?.let {
        val scopeOfApplyCall = parent.parent
        (scopeOfApplyCall == null || scopeOfApplyCall is KtBlockExpression)
                && (context == BindingContext.EMPTY || !it.isUsedAsExpression(context))
    } ?: false

private fun KtCallExpression.hasOnlyOneMemberAccessStatement(): Boolean {

    fun KtExpression.notAnAssignment() =
            safeAs<KtBinaryExpression>()
                    ?.operationToken != KtTokens.EQ

    fun KtExpression.isMemberAccess() =
            this is KtReferenceExpression ||
                    this is KtCallExpression ||
                    this.safeAs<KtDotQualifiedExpression>()?.receiverExpression is KtThisExpression

    val lambdaBody = firstLambdaArg?.bodyExpression
    if (lambdaBody?.children?.size == 1) {
        val expr = lambdaBody.statements[0]
        return expr.notAnAssignment() && expr.isMemberAccess()
    }
    return false
}

private const val APPLY_LITERAL = "apply"

private fun KtCallExpression.isApplyExpr() = calleeExpression?.textMatches(APPLY_LITERAL) == true

private val KtCallExpression.firstLambdaArg
    get() = lambdaArguments.firstOrNull()?.getLambdaExpression()
