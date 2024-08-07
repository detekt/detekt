package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext.DECLARATION_TO_DESCRIPTOR
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

/**
 * This rule checks whether overriding methods invoke the super method when the super method has a specific annotation.
 *
 * <noncompliant>
 * open class ParentClass {
 *     @CallSuper
 *     open fun someMethod(arg: Int) {
 *     }
 * }
 * class MyClass : ParentClass() {
 *     override fun someMethod(arg: Int) {
 *         doSomething()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * open class ParentClass {
 *     @CallSuper
 *     open fun someMethod(arg: Int) {
 *     }
 * }
 * class MyClass : ParentClass() {
 *     override fun someMethod(arg: Int) {
 *         super.someMethod(arg)
 *         doSomething()
 *     }
 * }
 * </compliant>
 */
class MissingSuperCall(config: Config) :
    Rule(
        config,
        "Overriding method is missing a call to overridden super method.",
    ),
    RequiresTypeResolution {
    @Configuration("Annotations to require that overriding methods invoke the super method")
    private val mustInvokeSuperAnnotations: List<FqName> by config(
        listOf(
            "androidx.annotation.CallSuper",
            "javax.annotation.OverridingMethodsMustInvokeSuper",
        )
    ) { it.map(::FqName) }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
        val functionDescriptor = bindingContext[DECLARATION_TO_DESCRIPTOR, function] as? CallableDescriptor ?: return
        val superFunctionDescriptor = functionDescriptor.superFunctionWithAnnotation() ?: return
        if (function.hasSuperCall(superFunctionDescriptor)) return

        report(CodeSmell(Entity.from(function), "Overriding method is missing a call to overridden super method."))
    }

    private fun CallableDescriptor.superFunctionWithAnnotation(): CallableDescriptor? =
        overriddenDescriptors.firstOrNull { d -> d.annotations.any { it.fqName in mustInvokeSuperAnnotations } }
            ?: overriddenDescriptors.firstNotNullOfOrNull { it.superFunctionWithAnnotation() }

    private fun KtNamedFunction.hasSuperCall(superFunctionDescriptor: CallableDescriptor): Boolean =
        anyDescendantOfType<KtQualifiedExpression> {
            it.getResolvedCall(bindingContext)?.resultingDescriptor == superFunctionDescriptor
        }
}
