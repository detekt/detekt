package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.receiverIsUsed
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * `apply` expressions are used frequently, but sometimes their usage should be replaced with
 * an ordinary method/extension function call to reduce visual complexity
 *
 * <noncompliant>
 * config.apply { version = "1.2" } // can be replaced with `config.version = "1.2"`
 * config?.apply { environment = "test" } // can be replaced with `config?.environment = "test"`
 * config?.apply { println(version) } // `apply` can be replaced by `let`
 * </noncompliant>
 *
 * <compliant>
 * config.apply {
 *     version = "1.2"
 *     environment = "test"
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class UnnecessaryApply(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "The `apply` usage is unnecessary", Debt.FIVE_MINS)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        if (expression.isApplyExpr() &&
                expression.hasOnlyOneMemberAccessStatement() &&
                !expression.receiverIsUsed(bindingContext)) {
            val message = if (expression.parent is KtSafeQualifiedExpression) {
                "apply can be replaced with let or an if"
            } else {
                "apply expression can be omitted"
            }
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }
}

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
