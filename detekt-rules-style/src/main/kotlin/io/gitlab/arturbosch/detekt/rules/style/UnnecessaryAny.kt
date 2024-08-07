package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.firstParameter
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.contracts.parsing.isEqualsDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl.WithDestructuringDeclaration
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

/**
 * Turn on this rule to flag usage of `any` which can either be replaced with simple `contains` call
 * or can removed entirely to reduce visual complexity.
 *
 * <noncompliant>
 * val a = 1
 * list.any { it == a }
 * </noncompliant>
 *
 * <compliant>
 * val a = 1
 * list.contains(a)
 * </compliant>
 */
class UnnecessaryAny(config: Config) :
    Rule(
        config,
        "The `any {  }` usage is unnecessary."
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCallingAny()) return

        val msg = shouldBeReported(expression)
        if (msg != null) {
            report(
                CodeSmell(
                    Entity.from(expression),
                    msg
                )
            )
        }
    }

    @Suppress("ReturnCount")
    private fun shouldBeReported(expression: KtCallExpression): String? {
        val valueArgument = expression.valueArguments.getOrNull(0) ?: return null
        return when (val valueExpression = valueArgument.getArgumentExpression()) {
            is KtLambdaExpression -> {
                val bodyExpression = valueExpression.bodyExpression ?: return null
                val descriptor =
                    valueExpression.firstParameter(bindingContext) ?: return null

                bodyExpression.shouldBlockExpressionBeReported(descriptor)
            }

            is KtNamedFunction -> {
                val descriptor =
                    bindingContext[
                        BindingContext.DECLARATION_TO_DESCRIPTOR, valueExpression.valueParameters[0]
                    ] as? VariableDescriptor ?: return null
                val bodyExpression =
                    valueExpression.bodyExpression as? KtBlockExpression
                        ?: return valueExpression.bodyExpression?.shouldStatementBeReported(
                            descriptor,
                        )
                bodyExpression.shouldBlockExpressionBeReported(descriptor)
            }

            else -> {
                null
            }
        }
    }

    private fun KtBlockExpression.shouldBlockExpressionBeReported(
        descriptor: VariableDescriptor
    ): String? {
        if (this.statements.isEmpty()) return null
        if (descriptor is WithDestructuringDeclaration) {
            return if (descriptor.destructuringVariables.all { getItUsageCount(it) == 0 }) {
                ANY_CAN_BE_OMITTED_MSG
            } else {
                null
            }
        }
        if (this.statements.isNotEmpty() && getItUsageCount(descriptor) == 0) return ANY_CAN_BE_OMITTED_MSG

        val firstStatement = this.statements[0]
        val statement = if (firstStatement is KtReturnExpression) {
            firstStatement.returnedExpression
        } else {
            firstStatement
        } ?: return null

        return statement.shouldStatementBeReported(descriptor)
    }

    private fun KtExpression.shouldStatementBeReported(descriptor: VariableDescriptor): String? =
        when {
            this is KtBinaryExpression && operationToken == KtTokens.EQEQ -> {
                isUsageOfValueAndItEligible(descriptor, left, right)
            }

            this is KtDotQualifiedExpression && selectorExpression.isCallingEquals() -> {
                isUsageOfValueAndItEligible(
                    descriptor,
                    receiverExpression,
                    (selectorExpression as? KtCallExpression)?.valueArguments?.getOrNull(0)
                        ?.getArgumentExpression()
                )
            }

            else -> {
                if (this.getItUsageCount(descriptor) <= 0) ANY_CAN_BE_OMITTED_MSG else null
            }
        }

    @Suppress("ReturnCount")
    private fun isUsageOfValueAndItEligible(
        descriptor: VariableDescriptor,
        leftExpression: KtExpression?,
        rightExpression: KtExpression?
    ): String? {
        leftExpression ?: return null
        rightExpression ?: return null

        val itRefCountInLeft = leftExpression.getItUsageCount(descriptor)
        val itRefCountInRight = rightExpression.getItUsageCount(descriptor)
        return when {
            itRefCountInLeft > 0 && itRefCountInRight > 0 -> {
                // both side `it` has been used
                null
            }

            itRefCountInLeft == 0 && itRefCountInRight == 0 -> {
                // no side has `it`
                ANY_CAN_BE_OMITTED_MSG
            }

            itRefCountInRight == 1 -> {
                // reversing the order of parameter
                isUsageOfValueAndItEligible(descriptor, rightExpression, leftExpression)
            }

            itRefCountInLeft == 1 -> {
                val valueExpressionType =
                    rightExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType ?: return null
                val itExpressionType =
                    leftExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType ?: return null
                if (leftExpression is KtReferenceExpression &&
                    valueExpressionType.isSubtypeOf(itExpressionType)
                ) {
                    USE_CONTAINS_MSG
                } else {
                    null
                }
            }

            else -> {
                null
            }
        }
    }

    private fun KtExpression.getItUsageCount(descriptor: VariableDescriptor) =
        collectDescendantsOfType<KtNameReferenceExpression>().count {
            bindingContext[BindingContext.REFERENCE_TARGET, it] == descriptor
        }

    private fun KtCallExpression.isCallingAny(): Boolean = isCalling(anyFqName, bindingContext)

    private fun KtExpression?.isCallingEquals(): Boolean {
        this ?: return false
        val resolvedCall = this.getResolvedCall(bindingContext) ?: return false
        return resolvedCall.resultingDescriptor.isEqualsDescriptor()
    }

    companion object {
        private val anyFqName = FqName("kotlin.collections.any")
        private const val USE_CONTAINS_MSG =
            "Use `contains` instead of `any {  }` call to check the presence of the element"
        private const val ANY_CAN_BE_OMITTED_MSG = "`any {  }` expression can be omitted"
    }
}
