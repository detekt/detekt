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
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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

        if (expression.isApplyExpr() &&
                expression.hasOnlyOneStatement() &&
                expression.receiverIsUnused()) {
            report(CodeSmell(
                    issue, Entity.from(expression),
                    "apply expression can be omitted"
            ))
        }
    }
}

private fun KtCallExpression.receiverIsUnused(): Boolean {
    if (parent is KtDotQualifiedExpression) {
        val scopeOfApplyCall = parent.parent
        return scopeOfApplyCall == null || scopeOfApplyCall is KtBlockExpression
    }
    return false
}

private fun KtCallExpression.hasOnlyOneStatement(): Boolean {
    val lambdaBody = firstLambdaArg?.bodyExpression
    if (lambdaBody.hasOnlyOneStatement()) {
        val expr = lambdaBody.statements[0]
        val notAnAssignment = expr
                ?.safeAs<KtBinaryExpression>()
                ?.operationToken != KtTokens.EQ
        val isMemberAccess = expr is KtReferenceExpression ||
                expr is KtCallExpression ||
                expr.safeAs<KtDotQualifiedExpression>()?.receiverExpression is KtThisExpression
        return notAnAssignment && isMemberAccess
    }
    return false
}

private const val APPLY_LITERAL = "apply"

private fun KtCallExpression.isApplyExpr() = calleeExpression?.textMatches(APPLY_LITERAL) == true

private val KtCallExpression.firstLambdaArg
    get() = lambdaArguments.firstOrNull()
            ?.getLambdaExpression()

@UseExperimental(ExperimentalContracts::class)
private fun KtBlockExpression?.hasOnlyOneStatement(): Boolean {
    contract {
        returns(true) implies (this@hasOnlyOneStatement != null)
    }
    return this?.children?.size == 1
}
