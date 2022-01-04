package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.tooling.api.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.ConfigAware
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Suppress any issue raised under a function definition that matches the signatures defined at `ignoreFunction`.
 *
 * *Note*: this Suppressor doesn't suppress issues found when you call these functions. It just suppresses the ones in
 * the function **definition**.
 *
 * @config ignoreFunction: List<String> The signature of the function. You can ignore all the overloads of a function
 * defining just is name like `java.time.LocalDate.now` or you can specify the parameters to only suppress one:
 * `java.time.LocalDate(java.time.Clock)`.
 *
 * *Note:* you need to write all the types with fully qualified names. For example `org.example.foo(kotlin.String)`. It
 * is important to add the `kotlin.String`. Only with `String` will not work.
 */
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
