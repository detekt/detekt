package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

/**
 * This rule warns on instances where a function, annotated with either `@CheckReturnValue` or `@CheckResult`,
 * returns a value but that value is not used in any way. The Kotlin compiler gives no warning for this scenario
 * normally so that's the rationale behind this rule.
 *
 * fun returnsValue() = 42
 * fun returnsNoValue() {}
 *
 * <noncompliant>
 *     returnsValue()
 * </noncompliant>
 *
 * <compliant>
 *     if (42 == returnsValue()) {}
 *     val x = returnsValue()
 * </compliant>
 *
 * @configuration restrictToAnnotatedMethods - if the rule should check only annotated methods. (default: `true`)
 * @configuration returnValueAnnotations - List of glob patterns to be used as inspection annotation (default: `['*.CheckReturnValue', '*.CheckResult']`)
 *
 * @requiresTypeResolution
 */
class IgnoredReturnValue(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
            "IgnoredReturnValue",
            Severity.Defect,
            "This call returns a value which is ignored",
            Debt.TWENTY_MINS
    )

    private val annotationsRegexes = valueOrDefaultCommaSeparated(
                RETURN_VALUE_ANNOTATIONS,
                DEFAULT_RETURN_VALUE_ANNOTATIONS
            )
            .distinct()
            .map { it.simplePatternToRegex() }

    private val restrictToAnnotatedMethods: Boolean = valueOrDefault(
        RESTRICT_TO_ANNOTATED_METHODS,
        DEFAULT_RESTRICT_TO_ANNOTATED_METHODS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        if (expression.isUsedAsExpression(bindingContext)) return

        val resultingDescriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return
        if (resultingDescriptor.returnType?.isUnit() == true) return
        if (restrictToAnnotatedMethods) {
            val annotations = resultingDescriptor.annotations.mapNotNull { it.fqName?.asString() }
            if (annotations.none { annotation -> annotationsRegexes.any { it.matches(annotation) } }) {
                return
            }
        }

        val messageText = expression.calleeExpression?.text ?: expression.text
        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                message = "The call $messageText is returning a value that is ignored."
            )
        )
    }

    companion object {
        const val RESTRICT_TO_ANNOTATED_METHODS = "restrictToAnnotatedMethods"
        const val DEFAULT_RESTRICT_TO_ANNOTATED_METHODS = true
        const val RETURN_VALUE_ANNOTATIONS = "returnValueAnnotations"
        val DEFAULT_RETURN_VALUE_ANNOTATIONS = listOf("*.CheckReturnValue", "*.CheckResult")
    }
}
