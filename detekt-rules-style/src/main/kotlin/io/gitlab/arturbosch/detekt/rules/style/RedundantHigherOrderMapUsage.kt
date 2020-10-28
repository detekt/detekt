package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getTargetFunctionDescriptor
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

/**
 * Redundant maps add complexity to the code and accomplish nothing. They should be removed or replaced with the proper
 * operator.
 *
 * <noncompliant>
 * fun foo(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 *         .map { it }
 * }
 *
 * fun bar(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 *         .map {
 *             doSomething(it)
 *             it
 *         }
 * }
 *
 * fun baz(set: Set<Int>): List<Int> {
 *     return set.map { it }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 * }
 *
 * fun bar(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 *         .onEach {
 *             doSomething(it)
 *         }
 * }
 *
 * fun baz(set: Set<Int>): List<Int> {
 *     return set.toList()
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
@Suppress("ReturnCount")
class RedundantHigherOrderMapUsage(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "RedundantHigherOrderMapUsage",
        Severity.Style,
        "Checks for Redundant 'map' calls.",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val calleeExpression = expression.calleeExpression
        if (calleeExpression?.text != "map") return
        val functionLiteral = expression.lambda()?.functionLiteral
        val lambdaStatements = functionLiteral?.bodyExpression?.statements ?: return

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        if (resolvedCall.resultingDescriptor.fqNameOrNull() !in mapFqNames) return

        val receiverType = resolvedCall.extensionReceiver?.type ?: return
        val receiverIsList = receiverType.isInheritorOf(listFqName)
        val receiverIsSet = receiverType.isInheritorOf(setFqName)
        val receiverIsSequence = receiverType.isInheritorOf(sequenceFqName)
        if (!receiverIsList && !receiverIsSet && !receiverIsSequence) return

        if (!functionLiteral.isRedundant(lambdaStatements)) return

        val message = when {
            lambdaStatements.size != 1 -> "This 'map' call can be replaced with 'onEach' or 'forEach'."
            receiverIsSet -> "This 'map' call can be replaced with 'toList'."
            else -> "This 'map' call can be removed."
        }
        report(CodeSmell(issue, Entity.from(calleeExpression), message))
    }

    private fun KtCallExpression.lambda(): KtLambdaExpression? {
        val argument = lambdaArguments.singleOrNull() ?: valueArguments.singleOrNull()
        val lambda = argument?.getArgumentExpression()?.unpackFunctionLiteral() ?: return null
        if (lambda.valueParameters.firstOrNull()?.destructuringDeclaration != null) return null
        return lambda
    }

    private fun KotlinType.isInheritorOf(fqName: FqName): Boolean {
        return isTypeOf(fqName) || constructor.supertypes.any { it.isTypeOf(fqName) }
    }

    private fun KotlinType.isTypeOf(fqName: FqName): Boolean {
        return constructor.declarationDescriptor?.fqNameSafe == fqName
    }

    private fun KtFunctionLiteral.isRedundant(lambdaStatements: List<KtExpression>): Boolean {
        val lambdaDescriptor = bindingContext[BindingContext.FUNCTION, this] ?: return false
        val lambdaParameter = lambdaDescriptor.valueParameters.singleOrNull() ?: return false
        val lastStatement = lambdaStatements.lastOrNull() ?: return false
        if (!lastStatement.isReferenceTo(lambdaParameter)) return false
        val returnExpressions = collectDescendantsOfType<KtReturnExpression> {
            it != lastStatement && it.getTargetFunctionDescriptor(bindingContext) == lambdaDescriptor
        }
        return returnExpressions.all { it.isReferenceTo(lambdaParameter) }
    }

    private fun KtExpression.isReferenceTo(descriptor: ValueParameterDescriptor): Boolean {
        val nameReference = if (this is KtReturnExpression) {
            this.returnedExpression
        } else {
            this
        } as? KtNameReferenceExpression
        return nameReference?.getResolvedCall(bindingContext)?.resultingDescriptor == descriptor
    }

    companion object {
        private val mapFqNames = listOf(FqName("kotlin.collections.map"), FqName("kotlin.sequences.map"))
        private val listFqName = FqName("kotlin.collections.List")
        private val setFqName = FqName("kotlin.collections.Set")
        private val sequenceFqName = FqName("kotlin.sequences.Sequence")
    }
}
