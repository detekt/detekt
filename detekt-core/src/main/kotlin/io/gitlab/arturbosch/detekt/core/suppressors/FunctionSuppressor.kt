package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.tooling.api.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.ConfigAware
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

internal fun functionSuppressorFactory(rule: ConfigAware, bindingContext: BindingContext): Suppressor? {
    val functionMatchers = rule.valueOrDefault("ignoreFunction", emptyList<String>())
        .map(FunctionMatcher::fromFunctionSignature)
    return if (functionMatchers.isNotEmpty()) {
        Suppressor { finding ->
            val element = finding.entity.ktElement
            element != null && functionSuppressor(element, bindingContext, functionMatchers)
        }
    } else {
        null
    }
}

private fun functionSuppressor(
    element: KtElement,
    bindingContext: BindingContext,
    functionMatchers: List<FunctionMatcher>,
): Boolean {
    return element.isInFunctionNamed(bindingContext, functionMatchers)
}

private fun KtElement.isInFunctionNamed(
    bindingContext: BindingContext,
    functionMatchers: List<FunctionMatcher>,
): Boolean {
    return if (this is KtNamedFunction && functionMatchers.any { it.match(this, bindingContext) }) {
        true
    } else {
        getStrictParentOfType<KtNamedFunction>()?.isInFunctionNamed(bindingContext, functionMatchers) ?: false
    }
}
