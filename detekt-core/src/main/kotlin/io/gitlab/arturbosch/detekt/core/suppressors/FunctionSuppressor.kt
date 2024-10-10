package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.psi.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.Rule
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
 * defining just its name like `java.time.LocalDate.now` or you can specify the parameters to only suppress one:
 * `java.time.LocalDate(java.time.Clock)`.
 *
 * *Note:* you need to write all the types with fully qualified names e.g. `org.example.foo(kotlin.String)`. It
 * is important to add `kotlin.String`. Just adding `String` will not work.
 */
internal fun functionSuppressorFactory(rule: Rule, bindingContext: BindingContext): Suppressor? {
    val functionMatchers = rule.config.valueOrDefault("ignoreFunction", emptyList<String>())
        .map(FunctionMatcher::fromFunctionSignature)
    return if (functionMatchers.isNotEmpty()) {
        if (rule.isForbiddenSuppress()) {
            return null
        }
        Suppressor { finding ->
            functionSuppressor(finding.entity.ktElement, bindingContext, functionMatchers)
        }
    } else {
        null
    }
}

private fun functionSuppressor(
    element: KtElement,
    bindingContext: BindingContext,
    functionMatchers: List<FunctionMatcher>,
): Boolean = element.isInFunctionNamed(bindingContext, functionMatchers)

private fun KtElement.isInFunctionNamed(
    bindingContext: BindingContext,
    functionMatchers: List<FunctionMatcher>,
): Boolean =
    if (this is KtNamedFunction && functionMatchers.any { it.match(this, bindingContext) }) {
        true
    } else {
        getStrictParentOfType<KtNamedFunction>()?.isInFunctionNamed(bindingContext, functionMatchers) ?: false
    }
