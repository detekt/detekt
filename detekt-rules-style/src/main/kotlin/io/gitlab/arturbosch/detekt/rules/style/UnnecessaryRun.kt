package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.receiverIsUsed
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getImplicitReceiverValue
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * `run` expressions are used for executing the block and returning its result, but sometimes its
 * usage should be replaced with an ordinary method/extension function call to reduce visual
 * complexity
 *
 * <noncompliant>
 * config.run { version = "1.2" }
 * config?.run { environment = "test" }
 * val final = getNullableValue() ?: run { fallback }
 * </noncompliant>
 *
 * <compliant>
 * config.version = "1.2"
 * config?.environment = "test"
 * val final = getNullableValue() ?: fallback
 * stringBuilder.run {
 *     append("test")
 *     toString()
 * }
 * </compliant>
 */
@RequiresTypeResolution
class UnnecessaryRun(config: Config) : Rule(
    config,
    "The `run` usage is unnecessary and can be removed."
) {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (
            expression.isRunExpr() &&
            expression.hasOnlyOneMemberAccessStatement(expression.receiverIsUsed(bindingContext))
        ) {
            val message = if (expression.parent is KtSafeQualifiedExpression) {
                "`run` can be replaced with `let` or an `if`"
            } else {
                "`run` expression can be omitted"
            }
            report(CodeSmell(Entity.from(expression), message))
        }
    }

    private fun KtCallExpression.isRunExpr() =
        calleeExpression?.textMatches(RUN_FQ_NAME.shortName().asString()) == true &&
            getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() == RUN_FQ_NAME

    @Suppress("ReturnCount")
    private fun KtCallExpression.hasOnlyOneMemberAccessStatement(receiverIsUsed: Boolean): Boolean {
        val lambda =
            (
                lambdaArguments.firstOrNull()?.getLambdaExpression()
                    ?: valueArguments
                        .firstOrNull()
                        ?.getArgumentExpression()
                        ?.unpackFunctionLiteral()
                ) ?: return false
        var singleStatement = lambda.bodyExpression?.statements?.singleOrNull()
            ?: return false

        if (singleStatement is KtBinaryExpression) {
            // any assignment with run can't happen w/o run when return of run is used
            if (singleStatement.operationToken in KtTokens.ALL_ASSIGNMENTS && receiverIsUsed) {
                return false
            }

            // for an assignment expression only consider whether members on the LHS use the run{} context
            singleStatement = singleStatement.left ?: return false
        } else if (singleStatement !is KtThisExpression &&
            singleStatement !is KtReferenceExpression &&
            singleStatement !is KtDotQualifiedExpression
        ) {
            return false
        }

        val lambdaDescriptor =
            bindingContext[BindingContext.FUNCTION, lambda.functionLiteral] ?: return false
        return singleStatement.collectDescendantsOfType<KtNameReferenceExpression> {
            val resolvedCall = it.getResolvedCall(bindingContext)
            if (it.parent is KtThisExpression) {
                resolvedCall?.resultingDescriptor?.containingDeclaration == lambdaDescriptor
            } else {
                resolvedCall?.getImplicitReceiverValue()?.declarationDescriptor == lambdaDescriptor
            }
        }.size <= 1
    }
}

private val RUN_FQ_NAME = FqName("kotlin.run")
