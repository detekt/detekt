package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import io.gitlab.arturbosch.detekt.rules.LET_LITERAL
import io.gitlab.arturbosch.detekt.rules.receiverIsUsed
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * `let` expressions are used extensively in our code for null-checking and chaining functions,
 * but sometimes their usage should be replaced with a ordinary method/extension function call
 * to reduce visual complexity
 *
 * <noncompliant>
 * a.let { print(it) } // can be replaced with `print(a)`
 * a.let { it.plus(1) } // can be replaced with `a.plus(1)`
 * a?.let { it.plus(1) } // can be replaced with `a?.plus(1)`
 * a?.let { that -> that.plus(1) }?.let { it.plus(1) } // can be replaced with `a?.plus(1)?.plus(1)`
 * a.let { 1.plus(1) } // can be replaced with `1.plus(1)`
 * a?.let { 1.plus(1) } // can be replaced with `if (a != null) 1.plus(1)`
 * </noncompliant>
 *
 * <compliant>
 * a?.let { print(it) }
 * a?.let { 1.plus(it) } ?.let { msg -> print(msg) }
 * a?.let { it.plus(it) }
 * val b = a?.let { 1.plus(1) }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class UnnecessaryLet(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName, Severity.Style,
        "The `let` usage is unnecessary", Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        if (!expression.isLetExpr()) return

        val lambdaExpr = expression.firstLambdaArg

        val isNullSafeOperator = expression.parent is KtSafeQualifiedExpression

        if (lambdaExpr == null) {
            if (!isNullSafeOperator) {
                report(CodeSmell(issue, Entity.from(expression), "let expression can be omitted"))
            }
        } else {
            val lambdaReferenceCount = lambdaExpr.countReferences()
            if (lambdaReferenceCount == 0 && !expression.receiverIsUsed(bindingContext) && isNullSafeOperator) {
                report(
                    CodeSmell(
                        issue, Entity.from(expression),
                        "let expression can be replaces with a simple if"
                    )
                )
            } else if (lambdaReferenceCount <= 1 && !isNullSafeOperator) {
                report(CodeSmell(issue, Entity.from(expression), "let expression can be omitted"))
            } else if (lambdaReferenceCount == 1 && canBeReplacedWithCall(lambdaExpr)) {
                report(CodeSmell(issue, Entity.from(expression), "let expression can be omitted"))
            }
        }
    }
}

private fun canBeReplacedWithCall(lambdaExpr: KtLambdaExpression?): Boolean {
    val lambdaParameter = lambdaExpr?.firstParameter
    val lambdaBody = lambdaExpr?.bodyExpression

    if (lambdaBody?.hasOnlyOneStatement() != true) return false

    val exprReceiver = when (val firstExpr = lambdaBody.firstChild) {
        is KtDotQualifiedExpression -> firstExpr.receiverExpression
        is KtSafeQualifiedExpression -> firstExpr.receiverExpression
        else -> null
    }

    return when {
        exprReceiver == null -> false
        lambdaParameter == null -> exprReceiver.textMatches(IT_LITERAL)
        else -> {
            val destructuringDeclaration = lambdaParameter.destructuringDeclaration
            destructuringDeclaration?.entries?.any { exprReceiver.textMatches(it.nameAsSafeName.asString()) }
                ?: exprReceiver.textMatches(lambdaParameter.nameAsSafeName.asString())
        }
    }
}

private fun KtCallExpression.isLetExpr() = calleeExpression?.textMatches(LET_LITERAL) == true

private val KtCallExpression.firstLambdaArg get() = lambdaArguments.firstOrNull()?.getLambdaExpression()

private val KtLambdaExpression.firstParameter get() = valueParameters.firstOrNull()

private fun KtBlockExpression.hasOnlyOneStatement() = this.children.size == 1

private fun PsiElement.countVarRefs(varName: String): Int =
    children.sumBy { it.countVarRefs(varName) + if (it.textMatches(varName) && it !is ValueArgument) 1 else 0 }

private fun KtLambdaExpression.countReferences(): Int {
    val bodyExpression = bodyExpression ?: return 0
    val destructuringDeclaration = firstParameter?.destructuringDeclaration
    return if (destructuringDeclaration != null) {
        destructuringDeclaration.entries.sumBy { bodyExpression.countVarRefs(it.nameAsSafeName.asString()) }
    } else {
        val parameterName = firstParameter?.nameAsSafeName?.asString() ?: IT_LITERAL
        bodyExpression.countVarRefs(parameterName)
    }
}
