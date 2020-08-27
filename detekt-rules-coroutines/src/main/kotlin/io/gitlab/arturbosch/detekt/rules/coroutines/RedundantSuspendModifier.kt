package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * `suspend` modifier should only be used where needed, otherwise the function can only be used from other suspending
 * functions. This needlessly restricts use of the function and should be avoided by removing the `suspend` modifier
 * where it's not needed.
 *
 * Based on code from Kotlin project:
 * https://github.com/JetBrains/kotlin/blob/v1.3.61/idea/src/org/jetbrains/kotlin/idea/inspections/RedundantSuspendModifierInspection.kt
 *
 * <noncompliant>
 * suspend fun normalFunction() {
 *   println("string")
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun normalFunction() {
 *   println("string")
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class RedundantSuspendModifier(config: Config) : Rule(config) {

    override val issue = Issue(
        "RedundantSuspendModifier",
        Severity.Minor,
        "`suspend` modifier is only needed for functions that contain suspending calls",
        Debt.FIVE_MINS
    )

    @Suppress("detekt.ReturnCount")
    override fun visitNamedFunction(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY) return
        val suspendModifier = function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        if (!function.hasBody()) return
        if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return

        val descriptor = bindingContext[BindingContext.FUNCTION, function] ?: return
        if (descriptor.modality == Modality.OPEN) return

        if (function.anyDescendantOfType<KtExpression> { it.hasSuspendCalls() }) {
            return
        } else {
            report(CodeSmell(issue, Entity.from(suspendModifier), "Function has redundant `suspend` modifier."))
        }
    }

    @Suppress("detekt.ReturnCount")
    private fun KtExpression.isValidCandidateExpression(): Boolean {
        if (this is KtOperationReferenceExpression || this is KtForExpression) return true
        if (this is KtProperty || this is KtNameReferenceExpression) return true
        val parent = parent
        if (parent is KtCallExpression && parent.calleeExpression == this) return true
        if (this is KtCallExpression && this.calleeExpression is KtCallExpression) return true
        return false
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
                    val accessors = variableDescriptor?.accessors ?: emptyList()
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
                if ((resolvedCall?.resultingDescriptor as? FunctionDescriptor)?.isSuspend == true) true
                else {
                    val propertyDescriptor = resolvedCall?.resultingDescriptor as? PropertyDescriptor
                    val s = propertyDescriptor?.fqNameSafe?.asString()
                    s?.startsWith("kotlin.coroutines.") == true && s.endsWith(".coroutineContext")
                }
            }
        }
    }
}
