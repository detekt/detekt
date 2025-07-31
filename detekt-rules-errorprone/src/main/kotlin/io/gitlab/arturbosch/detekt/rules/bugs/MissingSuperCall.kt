package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

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
    RequiresAnalysisApi {

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

        analyze(function) {
            val superFunctionId = function.symbol.allOverriddenSymbols.firstOrNull {
                it.annotations.any { ann -> ann.classId?.asSingleFqName() in mustInvokeSuperAnnotations }
            }?.callableId ?: return

            val hasSuperCall = function.anyDescendantOfType<KtQualifiedExpression> {
                it.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId == superFunctionId
            }

            if (!hasSuperCall) {
                report(
                    Finding(
                        Entity.from(function),
                        "Overriding method is missing a call to overridden super method.",
                    ),
                )
            }
        }
    }
}
