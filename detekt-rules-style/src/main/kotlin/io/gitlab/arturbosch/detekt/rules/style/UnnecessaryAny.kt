package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.firstParameter
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.contracts.parsing.isEqualsDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
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
 * Turn on this rule to flag usage of `any` to check the presence of an element that can be
 * replaced with simpler `contains` call.
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
@RequiresTypeResolution
class UnnecessaryAny(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        "Use `contains` instead of `any {  }` call to check the presence of the element",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.isCallingAny() && shouldBeReported(expression)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Use `contains` instead of `any {  }` call to check the presence of the element"
                )
            )
        }
    }

    @Suppress("ReturnCount")
    private fun shouldBeReported(expression: KtCallExpression): Boolean {
        val valueArgument = expression.valueArguments.getOrNull(0) ?: return false
        return when (val valueExpression = valueArgument.getArgumentExpression()) {
            is KtLambdaExpression -> {
                val bodyExpression = valueExpression.bodyExpression ?: return false
                bodyExpression.isBodyContainsEligibleEqualityCheck(
                    valueExpression.firstParameter(bindingContext)
                )
            }

            is KtNamedFunction -> {
                val valueParameterDescriptor =
                    bindingContext[
                        BindingContext.DECLARATION_TO_DESCRIPTOR, valueExpression.valueParameters[0]
                    ] as? ValueParameterDescriptor ?: return false
                val bodyExpression =
                    valueExpression.bodyExpression as? KtBlockExpression
                        ?: return valueExpression.bodyExpression?.isEligibleEqualityCheck(
                            valueParameterDescriptor,
                        ) == true
                bodyExpression.isBodyContainsEligibleEqualityCheck(valueParameterDescriptor)
            }

            else -> {
                false
            }
        }
    }

    private fun KtBlockExpression.isBodyContainsEligibleEqualityCheck(itParameter: ValueParameterDescriptor?): Boolean {
        if (this.statements.isEmpty() || this.statements.size != 1) return false

        val firstStatement = this.statements[0]
        val statement = if (firstStatement is KtReturnExpression) {
            firstStatement.returnedExpression
        } else {
            firstStatement
        } ?: return false

        return statement.isEligibleEqualityCheck(itParameter)
    }

    private fun KtExpression.isEligibleEqualityCheck(itParameter: ValueParameterDescriptor?): Boolean {
        return when {
            this is KtBinaryExpression && operationToken == KtTokens.EQEQ -> {
                isUsageOfValueAndItCorrect(itParameter, left, right)
            }

            this is KtDotQualifiedExpression && selectorExpression.isCallingEquals() -> {
                isUsageOfValueAndItCorrect(
                    itParameter,
                    receiverExpression,
                    (selectorExpression as? KtCallExpression)?.valueArguments?.getOrNull(0)
                        ?.getArgumentExpression()
                )
            }

            else -> {
                false
            }
        }
    }

    @Suppress("ReturnCount")
    private fun isUsageOfValueAndItCorrect(
        itParameterDescriptor: ValueParameterDescriptor?,
        leftExpression: KtExpression?,
        rightExpression: KtExpression?
    ): Boolean {
        leftExpression ?: return false
        rightExpression ?: return true

        fun KtExpression.getItUsageCount() =
            collectDescendantsOfType<KtNameReferenceExpression>().count {
                bindingContext[BindingContext.REFERENCE_TARGET, it] == itParameterDescriptor
            }

        val itRefCountInLeft = leftExpression.getItUsageCount()
        val itRefCountInRight = rightExpression.getItUsageCount()
        return if (itIsPresentOnOneSide(itRefCountInLeft, itRefCountInRight)) {
            // both side `it` has been used or no side uses `it`
            false
        } else if (itRefCountInRight > 0) {
            // reversing the order of parameter
            isUsageOfValueAndItCorrect(itParameterDescriptor, rightExpression, leftExpression)
        } else {
            val valueExpressionType =
                rightExpression.getResolvedCall(bindingContext)?.getReturnType() ?: return false
            val itExpressionType =
                leftExpression.getResolvedCall(bindingContext)?.getReturnType() ?: return false
            leftExpression is KtReferenceExpression &&
                valueExpressionType.isSubtypeOf(itExpressionType)
        }
    }

    private fun itIsPresentOnOneSide(itRefCountInLeft: Int, itRefCountInRight: Int) =
        itRefCountInLeft > 0 && itRefCountInRight > 0 ||
            itRefCountInLeft == 0 && itRefCountInRight == 0

    private fun KtCallExpression.isCallingAny(): Boolean = isCalling(anyFqName, bindingContext)

    private fun KtExpression?.isCallingEquals(): Boolean {
        this ?: return false
        val resolvedCall = this.getResolvedCall(bindingContext) ?: return false
        return resolvedCall.resultingDescriptor.isEqualsDescriptor()
    }

    companion object {
        private val anyFqName = FqName("kotlin.collections.any")
    }
}
