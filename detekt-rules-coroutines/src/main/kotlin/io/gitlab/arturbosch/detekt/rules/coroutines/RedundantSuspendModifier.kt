package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
import org.jetbrains.kotlin.descriptors.accessors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingContext.DECLARATION_TO_DESCRIPTOR
import org.jetbrains.kotlin.resolve.BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL
import org.jetbrains.kotlin.resolve.BindingContext.LOOP_RANGE_HAS_NEXT_RESOLVED_CALL
import org.jetbrains.kotlin.resolve.BindingContext.LOOP_RANGE_ITERATOR_RESOLVED_CALL
import org.jetbrains.kotlin.resolve.BindingContext.LOOP_RANGE_NEXT_RESOLVED_CALL
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/*
 * Based on code from Kotlin project:
 * https://github.com/JetBrains/kotlin/blob/v1.3.61/idea/src/org/jetbrains/kotlin/idea/inspections/RedundantSuspendModifierInspection.kt
 */

/**
 * `suspend` modifier should only be used where needed, otherwise the function can only be used from other suspending
 * functions. This needlessly restricts use of the function and should be avoided by removing the `suspend` modifier
 * where it's not needed.
 *
 * <noncompliant>
 * suspend fun normalFunction() {
 *     println("string")
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun normalFunction() {
 *     println("string")
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class RedundantSuspendModifier(config: Config) :
    Rule(
        config,
        "The `suspend` modifier is only needed for functions that contain suspending calls."
    ),
    RequiresTypeResolution {
    override fun visitNamedFunction(function: KtNamedFunction) {
        val suspendModifier = function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        if (!function.hasBody()) return
        if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD) || function.hasModifier(KtTokens.ACTUAL_KEYWORD)) return

        val descriptor = bindingContext[BindingContext.FUNCTION, function] ?: return
        if (descriptor.modality == Modality.OPEN) return

        if (!function.anyDescendantOfType<KtExpression> { it.hasSuspendCalls() }) {
            report(CodeSmell(Entity.from(suspendModifier), "Function has redundant `suspend` modifier."))
        }
    }

    private fun KtExpression.isValidCandidateExpression(): Boolean =
        when (this) {
            is KtOperationReferenceExpression, is KtForExpression, is KtProperty, is KtNameReferenceExpression -> true
            else -> {
                val parent = parent
                if (parent is KtCallExpression && parent.calleeExpression == this) {
                    true
                } else {
                    this is KtCallExpression && this.calleeExpression is KtCallExpression
                }
            }
        }

    private fun KtExpression.hasSuspendCalls(): Boolean {
        if (!isValidCandidateExpression()) return false

        return when (this) {
            is KtForExpression -> {
                val iteratorResolvedCall = bindingContext[LOOP_RANGE_ITERATOR_RESOLVED_CALL, loopRange]
                val loopRangeHasNextResolvedCall = bindingContext[LOOP_RANGE_HAS_NEXT_RESOLVED_CALL, loopRange]
                val loopRangeNextResolvedCall = bindingContext[LOOP_RANGE_NEXT_RESOLVED_CALL, loopRange]
                listOf(iteratorResolvedCall, loopRangeHasNextResolvedCall, loopRangeNextResolvedCall).any {
                    it?.resultingDescriptor?.isSuspend == true
                }
            }
            is KtProperty -> {
                if (hasDelegateExpression()) {
                    val variableDescriptor =
                        bindingContext[DECLARATION_TO_DESCRIPTOR, this] as? VariableDescriptorWithAccessors
                    val accessors = variableDescriptor?.accessors.orEmpty()
                    accessors.any { accessor ->
                        val delegatedFunctionDescriptor =
                            bindingContext[DELEGATED_PROPERTY_RESOLVED_CALL, accessor]?.resultingDescriptor
                        delegatedFunctionDescriptor?.isSuspend == true
                    }
                } else {
                    false
                }
            }
            else -> {
                val resolvedCall = getResolvedCall(bindingContext)
                if ((resolvedCall?.resultingDescriptor as? FunctionDescriptor)?.isSuspend == true) {
                    true
                } else {
                    val propertyDescriptor = resolvedCall?.resultingDescriptor as? PropertyDescriptor
                    val s = propertyDescriptor?.fqNameSafe?.asString()
                    s?.startsWith("kotlin.coroutines.") == true && s.endsWith(".coroutineContext")
                }
            }
        }
    }
}
