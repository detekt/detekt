package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.builtins.getReceiverTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isExtensionFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi2ir.deparenthesize
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.VarargValueArgument
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyPackageDescriptor
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isDynamic
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

/**
 * Avoid creating unnecessary lambda to call a function where function reference can be used. This may have performance
 * penalty when compared to directly passing lambda variable.
 *
 * <noncompliant>
 * inline fun test(lambda: () -> Unit) {
 *     thread {
 *         lambda()
 *     }.start()
 * }
 *
 * val a: (Int) -> String = { it.toString() }
 *
 * fun toDomain(i: Int) = i.toString()
 * fun test() {
 *     listOf(1).map { toDomain(it) }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test(lambda: () -> Unit) {
 *     thread(block = lambda).start()
 * }
 *
 * val a: (Int) -> String = Int::toString
 *
 * fun toDomain(i: Int) = i.toString()
 * listOf(1).map { toDomain(it) }
 *
 * fun toDomain(i: Int) = i.toString()
 * fun test() {
 *     listOf(1).map(::toDomain)
 * }
 * </compliant>
 *
 *
 */
// Implementation taken from https://github.com/JetBrains/intellij-community/blob/master/plugins/kotlin/idea/src/org/jetbrains/kotlin/idea/intentions/ConvertLambdaToReferenceIntention.kt
@RequiresTypeResolution
class UnnecessaryLambda(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "UnnecessaryLambda",
        Severity.Performance,
        "Detects unnecessary lambda creation which can be avoided by directly using functional reference " +
            "or lambda reference",
        Debt.FIVE_MINS
    )

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val singleStatement = lambdaExpression.singleStatementOrNull()?.deparenthesize() ?: return
        val (isViolation, errorExpression) = when (singleStatement) {
            is KtCallExpression -> {
                isConvertibleCallInLambda(
                    callableExpression = singleStatement,
                    lambdaExpression = lambdaExpression
                ) to singleStatement
            }
            is KtNameReferenceExpression -> false to null // Global property reference is not possible (?!)
            is KtDotQualifiedExpression -> {
                val selector = singleStatement.selectorExpression ?: return
                isConvertibleCallInLambda(
                    callableExpression = selector,
                    explicitReceiver = singleStatement.receiverExpression,
                    lambdaExpression = lambdaExpression
                ) to singleStatement
            }
            else -> false to null
        }

        if (isViolation && errorExpression != null) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(errorExpression),
                    "Use the function reference instead of calling ${(errorExpression as KtExpression).text} " +
                        "method directly in a new lambda. This may result in a performance penalty."
                )
            )
        }
    }

    private fun KtExpression.isReferenceToPackage(): Boolean {
        val selectorOrThis = (this as? KtQualifiedExpression)?.selectorExpression ?: this
        if (selectorOrThis !is KtReferenceExpression) return false
        return bindingContext[BindingContext.REFERENCE_TARGET, selectorOrThis] is PackageViewDescriptor
    }

    private fun FunctionDescriptor.overloadedFunctions(): Collection<SimpleFunctionDescriptor> {
        val memberScope = when (val containingDeclaration = this.containingDeclaration) {
            is ClassDescriptor -> containingDeclaration.unsubstitutedMemberScope
            is LazyPackageDescriptor -> containingDeclaration.getMemberScope()
            else -> null
        }
        return memberScope?.getContributedFunctions(name, NoLookupLocation.FROM_IDE).orEmpty()
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod", "ReturnCount")
    private fun isConvertibleCallInLambda(
        callableExpression: KtExpression,
        explicitReceiver: KtExpression? = null,
        lambdaExpression: KtLambdaExpression,
    ): Boolean {
        val languageVersionSettings = compilerResources?.languageVersionSettings ?: return false
        val calleeReferenceExpression = when (callableExpression) {
            is KtCallExpression -> callableExpression.calleeExpression as? KtNameReferenceExpression ?: return false
            is KtNameReferenceExpression -> callableExpression
            else -> return false
        }

        if (explicitReceiver is KtSuperExpression || explicitReceiver?.isReferenceToPackage() == true) return false

        val calleeDescriptor =
            calleeReferenceExpression.getResolvedCall(bindingContext)?.resultingDescriptor as? CallableMemberDescriptor
                ?: return false

        val lambdaParameterType = lambdaExpression.lambdaParameterType(bindingContext)
        if (lambdaParameterType?.isExtensionFunctionType == true) {
            // if in case of extension lambda, there is explicit receiver then that must be `this` o/w callable ref
            // with same code not possible
            if (explicitReceiver != null && explicitReceiver !is KtThisExpression) return false
            if (lambdaParameterType.getReceiverTypeFromFunctionType() != calleeDescriptor.eligibleReceiverType()) {
                return false
            }
        }

        val lambdaParameterIsSuspend = lambdaParameterType?.isSuspendFunctionType == true
        val calleeFunctionIsSuspend = (calleeDescriptor as? FunctionDescriptor)?.isSuspend == true
        if (!lambdaParameterIsSuspend && calleeFunctionIsSuspend) return false
        if (lambdaParameterIsSuspend && !calleeFunctionIsSuspend &&
            !languageVersionSettings.supportsFeature(LanguageFeature.SuspendConversion)
        ) {
            return false
        }

        // No references with type parameters
        if (!checkTypeParameter(calleeDescriptor)) return false
        // No references to Java synthetic properties
        if (!languageVersionSettings.supportsFeature(LanguageFeature.ReferencesToSyntheticJavaProperties) &&
            calleeDescriptor is SyntheticJavaPropertyDescriptor
        ) {
            return false
        }

        val descriptorHasReceiver = with(calleeDescriptor) {
            // No references to both member / extension
            if (dispatchReceiverParameter != null && extensionReceiverParameter != null) return false
            dispatchReceiverParameter != null || extensionReceiverParameter != null
        }
        val noBoundReferences = !languageVersionSettings.supportsFeature(LanguageFeature.BoundCallableReferences)
        if (noBoundReferences && descriptorHasReceiver && explicitReceiver == null) return false

        val callableArgumentsCount = (callableExpression as? KtCallExpression)?.valueArguments?.size ?: 0
        if (calleeDescriptor.valueParameters.size != callableArgumentsCount &&
            (
                lambdaExpression.parentValueArgument() == null || calleeDescriptor.valueParameters.none {
                    it.declaresDefaultValue()
                }
                )
        ) {
            return false
        }

        if (!lambdaExpression.isArgument() &&
            calleeDescriptor is FunctionDescriptor &&
            calleeDescriptor.overloadedFunctions().size > 1
        ) {
            val property = lambdaExpression.getStrictParentOfType<KtProperty>()
            if (property != null && property.initializer?.deparenthesize() != lambdaExpression) return false
            val lambdaReturnType =
                bindingContext[BindingContext.EXPRESSION_TYPE_INFO, lambdaExpression]
                    ?.type
                    ?.arguments
                    ?.lastOrNull()
                    ?.type
            if (lambdaReturnType != calleeDescriptor.returnType) return false
        }

        val lambdaValueParameterDescriptors =
            bindingContext[BindingContext.FUNCTION, lambdaExpression.functionLiteral]?.valueParameters ?: return false
        if (explicitReceiver != null && explicitReceiver !is KtSimpleNameExpression &&
            explicitReceiver.anyDescendantOfType<KtSimpleNameExpression> {
                it.getResolvedCall(bindingContext)?.resultingDescriptor in lambdaValueParameterDescriptors
            }
        ) {
            return false
        }

        val explicitReceiverDescriptor =
            (explicitReceiver as? KtNameReferenceExpression)?.let {
                bindingContext[BindingContext.REFERENCE_TARGET, it]
            }
        val lambdaParameterAsExplicitReceiver = when (noBoundReferences) {
            true -> {
                explicitReceiver != null
            }
            false -> {
                explicitReceiverDescriptor != null &&
                    explicitReceiverDescriptor == lambdaValueParameterDescriptors.firstOrNull()
            }
        }
        val explicitReceiverShift = if (lambdaParameterAsExplicitReceiver) 1 else 0

        val lambdaParametersCount = lambdaValueParameterDescriptors.size
        if (lambdaParametersCount != callableArgumentsCount + explicitReceiverShift) return false

        if (explicitReceiver != null &&
            explicitReceiverDescriptor is ValueDescriptor &&
            lambdaParameterAsExplicitReceiver
        ) {
            val receiverType = explicitReceiverDescriptor.type
            // No exotic receiver types
            @Suppress("ComplexCondition")
            if (receiverType.isTypeParameter() ||
                receiverType.isError ||
                receiverType.isDynamic() ||
                !receiverType.constructor.isDenotable ||
                receiverType.isFunctionType
            ) {
                return false
            }
        }

        // Same lambda / references function parameter order
        if (callableExpression is KtCallExpression) {
            if (lambdaValueParameterDescriptors.size < explicitReceiverShift + callableExpression.valueArguments.size) {
                return false
            }
            val resolvedCall = callableExpression.getResolvedCall(bindingContext) ?: return false
            resolvedCall.valueArguments.entries.forEach { (valueParameter, resolvedArgument) ->
                if (resolvedArgument is DefaultValueArgument) return@forEach
                val argument = resolvedArgument.arguments.singleOrNull() ?: return false
                if (resolvedArgument is VarargValueArgument && argument.getSpreadElement() == null) return false
                val argumentExpression = argument.getArgumentExpression() as? KtNameReferenceExpression ?: return false
                val argumentTarget =
                    bindingContext[BindingContext.REFERENCE_TARGET, argumentExpression] as? ValueParameterDescriptor
                        ?: return false
                if (argumentTarget != lambdaValueParameterDescriptors[valueParameter.index + explicitReceiverShift]) {
                    return false
                }
            }
        }
        return true
    }

    private fun checkTypeParameter(
        calleeDescriptor: CallableMemberDescriptor,
    ): Boolean {
        // <changed> we will ignore lambdas with type parameters as bringing that code from intellij-community
        // will require lots of code related to `InsertExplicitTypeArgumentsIntention`, `analyzeAsReplacement`
        // and `diagnostics`
        return calleeDescriptor.typeParameters.isEmpty()
    }

    private fun CallableDescriptor.eligibleReceiverType(): KotlinType? {
        return this.extensionReceiverParameter?.type ?: this.dispatchReceiverParameter?.type
    }

    @Suppress("ReturnCount")
    private fun KtLambdaExpression.lambdaParameterType(context: BindingContext): KotlinType? {
        val argument = parentValueArgument() ?: return null
        val callExpression = argument.getStrictParentOfType<KtCallExpression>() ?: return null
        return callExpression
            .getResolvedCall(context)
            ?.getParameterForArgument(argument)
            ?.type
    }

    private fun KtLambdaExpression.parentValueArgument(): KtValueArgument? {
        return if (parent is KtLabeledExpression) {
            parent.parent
        } else {
            parent
        } as? KtValueArgument
    }

    private fun KtLambdaExpression.singleStatementOrNull() = bodyExpression?.statements?.singleOrNull()

    private fun KtLambdaExpression.isArgument() =
        this === getStrictParentOfType<KtValueArgument>()?.getArgumentExpression()?.deparenthesize()
}
