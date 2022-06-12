package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.types.isFlexible

/*
 * Based on code from Kotlin project:
 * https://github.com/JetBrains/kotlin/blob/1.3.50/idea/src/org/jetbrains/kotlin/idea/intentions/SpecifyTypeExplicitlyIntention.kt#L86-L107
 */
/**
 * Platform types must be declared explicitly in public APIs to prevent unexpected errors.
 *
 * <noncompliant>
 * class Person {
 *     fun apiCall() = System.getProperty("propertyName")
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Person {
 *     fun apiCall(): String = System.getProperty("propertyName")
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class HasPlatformType(config: Config) : Rule(config) {

    override val issue = Issue(
        "HasPlatformType",
        Severity.Maintainability,
        "Platform types must be declared explicitly in public APIs.",
        Debt.FIVE_MINS
    )

    /*
    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)
        if (!expression.hasInterpolation()) return
        val candidateExpressions = mutableMapOf<CallableDescriptor, KtExpression>()
        expression.entries
            .asSequence()
            .filterIsInstance<KtBlockStringTemplateEntry>()
            .flatMap {
                it.expression?.collectDescendantsOfType<KtExpression>().orEmpty()
            }.forEach { blockExpression ->
                when (blockExpression) {
                    is KtCallExpression -> {
                        val descriptor = blockExpression.getResolvedCall(bindingContext)
                            ?.resultingDescriptor
                        if (descriptor != null && descriptor.returnType?.isFlexible() == true) {
                            candidateExpressions[descriptor] = blockExpression
                        }
                    }
                    is KtDotQualifiedExpression -> {
                        val descriptor = blockExpression.getResolvedCall(bindingContext)
                            ?.resultingDescriptor
                        if (descriptor != null && descriptor.returnType?.isFlexible() == true) {
                            candidateExpressions[descriptor] = blockExpression
                        }
                    }
                    is KtPostfixExpression -> {
                        blockExpression.baseExpression
                            ?.getResolvedCall(bindingContext)
                            ?.resultingDescriptor
                            ?.let(candidateExpressions::remove)
                    }
                    is KtSafeQualifiedExpression -> {
                        blockExpression.receiverExpression
                            .getResolvedCall(bindingContext)
                            ?.resultingDescriptor
                            ?.let(candidateExpressions::remove)
                    }
                    is KtBinaryExpression -> {
                        if (blockExpression.operationToken == KtTokens.ELVIS) {
                            blockExpression.left
                                ?.getResolvedCall(bindingContext)
                                ?.resultingDescriptor
                                ?.let(candidateExpressions::remove)
                        }
                    }
                }
            }

        candidateExpressions.forEach { (_, candidateExpression) ->
            report(
                CodeSmell(
                    issue,
                    Entity.from(candidateExpression),
                    "$expression has implicit platform type. Type must be declared explicitly."
                )
            )
        }
    }
    */

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        fun evaluateValueArgument(argument: KtValueArgument) {
            if (argument.isPlatformTypeCall()) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(argument),
                        "$expression uses implicit platform type in arguments."
                    )
                )
            }
        }

        val descriptor = expression.getResolvedCall(bindingContext)
            ?.resultingDescriptor ?: return
        val valueArguments = expression.valueArguments
        descriptor.valueParameters
            .asSequence()
            .mapIndexedNotNull { index, valueParameterDescriptor ->
                if (valueParameterDescriptor.returnType?.isMarkedNullable != true) {
                    index to valueParameterDescriptor.isVararg
                } else {
                    null
                }
            }.forEach { (index, isVarArg) ->
                if (isVarArg) {
                    valueArguments.subList(index, valueArguments.size).forEach(::evaluateValueArgument)
                } else {
                    valueArguments.getOrNull(index)?.let(::evaluateValueArgument)
                }
            }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        val receiverExpression = expression.receiverExpression
        if (receiverExpression.getType(bindingContext)?.isFlexible() == true) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(receiverExpression),
                    "'${receiverExpression.text}' has implicit platform type. Type must be declared explicitly."
                )
            )
        }
    }

    override fun visitKtElement(element: KtElement) {
        super.visitKtElement(element)
        if (bindingContext == BindingContext.EMPTY) return

        if (element is KtCallableDeclaration && element.hasImplicitPlatformType()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(element),
                    "$element has implicit platform type. Type must be declared explicitly."
                )
            )
        }
    }

    private fun KtValueArgument.isPlatformTypeCall(): Boolean {
        return this.getArgumentExpression()
            ?.let { it as? KtDotQualifiedExpression ?: it as? KtCallExpression }
            ?.getResolvedCall(bindingContext)
            ?.getReturnType()
            ?.isFlexible() == true
    }

    private fun KtCallableDeclaration.hasImplicitPlatformType(): Boolean {
        fun isPlatFormType(): Boolean {
            if (containingClassOrObject?.isLocal == true) return false
            val callable =
                bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this] as? CallableDescriptor ?: return false

            val isPublicApi = callable.visibility.isPublicAPI
            val isReturnTypeFlexible = callable.returnType?.isFlexible()
            return isPublicApi && isReturnTypeFlexible == true
        }

        return when (this) {
            is KtFunction -> !isLocal && !hasDeclaredReturnType() && isPlatFormType()
            is KtProperty -> !isLocal && typeReference == null && isPlatFormType()
            else -> false
        }
    }
}
