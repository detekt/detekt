package dev.detekt.core.suppressors

import dev.detekt.api.Rule
import dev.detekt.psi.FunctionMatcher
import dev.detekt.tooling.api.AnalysisMode
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

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
internal fun functionSuppressorFactory(rule: Rule, analysisMode: AnalysisMode): Suppressor? {
    val functionMatchers = rule.config.valueOrDefault("ignoreFunction", emptyList<String>())
        .map(FunctionMatcher::fromFunctionSignature)
    return if (functionMatchers.isNotEmpty()) {
        Suppressor { finding ->
            functionSuppressor(finding.entity.ktElement, functionMatchers, analysisMode)
        }
    } else {
        null
    }
}

private fun functionSuppressor(
    element: KtElement,
    functionMatchers: List<FunctionMatcher>,
    analysisMode: AnalysisMode,
): Boolean = element.isInFunctionNamed(functionMatchers, analysisMode == AnalysisMode.full)

private fun KtElement.isInFunctionNamed(functionMatchers: List<FunctionMatcher>, fullAnalysis: Boolean): Boolean =
    if (this is KtNamedFunction && functionMatchers.any { it.match(this, fullAnalysis) }) {
        true
    } else {
        getStrictParentOfType<KtNamedFunction>()?.isInFunctionNamed(functionMatchers, fullAnalysis) == true
    }
