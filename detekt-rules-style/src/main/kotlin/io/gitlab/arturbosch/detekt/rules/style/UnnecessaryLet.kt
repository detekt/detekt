package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import io.gitlab.arturbosch.detekt.rules.firstParameter
import io.gitlab.arturbosch.detekt.rules.isCalling
import io.gitlab.arturbosch.detekt.rules.receiverIsUsed
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl.WithDestructuringDeclaration
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * `let` expressions are used extensively in our code for null-checking and chaining functions,
 * but sometimes their usage should be replaced with an ordinary method/extension function call
 * to reduce visual complexity.
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
 */
@RequiresTypeResolution
class UnnecessaryLet(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "The `let` usage is unnecessary.",
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCalling(letFqName, bindingContext)) return

        val lambdaExpr = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()
        val referenceCount = lambdaExpr?.countLambdaParameterReference(bindingContext) ?: 0
        if (referenceCount > 1) return

        if (expression.parent is KtSafeQualifiedExpression) {
            if (lambdaExpr != null) {
                when {
                    referenceCount == 0 && !expression.receiverIsUsed(bindingContext) ->
                        report(expression, "let expression can be replaced with a simple if")

                    referenceCount == 1 && canBeReplacedWithCall(lambdaExpr) ->
                        report(expression, "let expression can be omitted")
                }
            }
        } else {
            if (referenceCount == 0 || canBeReplacedWithCall(lambdaExpr) || !expression.inCallChains()) {
                report(expression, "let expression can be omitted")
            }
        }
    }

    private fun report(expression: KtCallExpression, message: String) {
        report(CodeSmell(issue, Entity.from(expression), message))
    }

    companion object {
        private val letFqName = FqName("kotlin.let")
    }
}

private fun KtCallExpression.inCallChains(): Boolean {
    val qualified = parent as? KtQualifiedExpression ?: return false
    return qualified.parent is KtQualifiedExpression || qualified.receiverExpression is KtQualifiedExpression
}

private fun canBeReplacedWithCall(lambdaExpr: KtLambdaExpression?): Boolean {
    if (lambdaExpr == null) return false

    val receiver = when (val statement = lambdaExpr.bodyExpression?.statements?.singleOrNull()) {
        is KtQualifiedExpression -> statement.receiverExpression
        else -> null
    } ?: return false

    val lambdaParameter = lambdaExpr.valueParameters.singleOrNull()
    val lambdaParameterNames = if (lambdaParameter == null) {
        listOf(IT_LITERAL)
    } else {
        lambdaParameter.destructuringDeclaration?.entries.orEmpty()
            .plus(lambdaParameter)
            .map { it.nameAsSafeName.asString() }
    }
    return lambdaParameterNames.any { receiver.textMatches(it) }
}

private fun KtLambdaExpression.countLambdaParameterReference(context: BindingContext): Int {
    val bodyExpression = bodyExpression ?: return 0
    val firstParameter = firstParameter(context) ?: return 0

    val parameters = if (firstParameter is WithDestructuringDeclaration) {
        firstParameter.destructuringVariables
    } else {
        listOf(firstParameter)
    }

    return parameters.sumOf { parameter ->
        bodyExpression.collectDescendantsOfType<KtSimpleNameExpression> {
            context[BindingContext.REFERENCE_TARGET, it] == parameter
        }.count()
    }
}
