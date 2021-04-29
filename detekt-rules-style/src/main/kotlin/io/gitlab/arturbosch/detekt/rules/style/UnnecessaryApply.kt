package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.receiverIsUsed
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getImplicitReceiverValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.16.0")
class UnnecessaryApply(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "The `apply` usage is unnecessary",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        if (expression.isApplyExpr() &&
            expression.hasOnlyOneMemberAccessStatement() &&
            !expression.receiverIsUsed(bindingContext)
        ) {
            val message = if (expression.parent is KtSafeQualifiedExpression) {
                "apply can be replaced with let or an if"
            } else {
                "apply expression can be omitted"
            }
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }

    private fun KtCallExpression.isApplyExpr() = calleeExpression?.textMatches(APPLY_LITERAL) == true &&
        getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() == APPLY_FQ_NAME

    @Suppress("ReturnCount")
    private fun KtCallExpression.hasOnlyOneMemberAccessStatement(): Boolean {
        val lambda = lambdaArguments.firstOrNull()?.getLambdaExpression() ?: return false
        val singleStatement = lambda.bodyExpression?.statements?.singleOrNull() ?: return false
        if (singleStatement !is KtThisExpression &&
            singleStatement !is KtReferenceExpression &&
            singleStatement !is KtDotQualifiedExpression
        ) return false
        val lambdaDescriptor = bindingContext[BindingContext.FUNCTION, lambda.functionLiteral] ?: return false
        return singleStatement.collectDescendantsOfType<KtNameReferenceExpression> {
            val resolvedCall = it.getResolvedCall(bindingContext)
            if (it.parent is KtThisExpression) {
                resolvedCall?.resultingDescriptor?.containingDeclaration == lambdaDescriptor
            } else {
                resolvedCall?.getImplicitReceiverValue()?.declarationDescriptor == lambdaDescriptor
            }
        }.size == 1
    }
}

private const val APPLY_LITERAL = "apply"

private val APPLY_FQ_NAME = FqName("kotlin.apply")
