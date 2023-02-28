package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithFallback
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Reports empty functions. Empty blocks of code serve no purpose and should be removed.
 * This rule will not report functions with the override modifier that have a comment as their only body contents
 * (e.g., a `// no-op` comment in an unused listener function).
 *
 * Set the `ignoreOverridden` parameter to `true` to exclude all functions which are overriding other
 * functions from the superclass or from an interface (i.e., functions declared with the override modifier).
 */
@ActiveByDefault(since = "1.0.0")
class EmptyFunctionBlock(config: Config) : EmptyRule(config) {

    @Configuration("Excludes all the overridden functions")
    @Deprecated("Use `ignoreOverridden` instead")
    private val ignoreOverriddenFunctions: Boolean by config(false)

    @Suppress("DEPRECATION")
    @OptIn(UnstableApi::class)
    @Configuration("Excludes all the overridden functions")
    private val ignoreOverridden: Boolean by configWithFallback(::ignoreOverriddenFunctions, false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isOpen() || function.isDefaultFunction()) {
            return
        }
        val bodyExpression = function.bodyExpression
        if (!ignoreOverridden) {
            if (function.isOverride()) {
                bodyExpression?.addFindingIfBlockExprIsEmptyAndNotCommented()
            } else {
                bodyExpression?.addFindingIfBlockExprIsEmpty()
            }
        } else if (!function.isOverride()) {
            bodyExpression?.addFindingIfBlockExprIsEmpty()
        }
    }

    private fun KtNamedFunction.isDefaultFunction() =
        getParentOfType<KtClass>(true)?.isInterface() == true && hasBody()
}
