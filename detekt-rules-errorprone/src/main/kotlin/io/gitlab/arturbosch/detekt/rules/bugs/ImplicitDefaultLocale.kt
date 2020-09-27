package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.builtins.KotlinBuiltIns.isStringOrNullableString
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/**
 * Prefer passing [java.util.Locale] explicitly than using implicit default value when formatting
 * strings or performing a case conversion.
 *
 * The default locale is almost always not appropriate for machine-readable text like HTTP headers.
 * For example, if locale with tag `ar-SA-u-nu-arab` is a current default then `%d` placeholders
 * will be evaluated to numbers consisting of Eastern-Arabic (non-ASCII) digits.
 * [java.util.Locale.US] is recommended for machine-readable output.
 *
 * <noncompliant>
 * String.format("Timestamp: %d", System.currentTimeMillis())
 *
 * val str: String = getString()
 * str.toUpperCase()
 * str.toLowerCase()
 * </noncompliant>
 *
 * <compliant>
 * String.format(Locale.US, "Timestamp: %d", System.currentTimeMillis())
 *
 * val str: String = getString()
 * str.toUpperCase(Locale.US)
 * str.toLowerCase(Locale.US)
 * </compliant>
 */
class ImplicitDefaultLocale(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
            "ImplicitDefaultLocale", Severity.CodeSmell,
            "Implicit default locale used for string processing. Consider using explicit locale.",
            Debt.FIVE_MINS
    )

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        checkStringFormatting(expression)
        checkCaseConversion(expression)
    }

    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)
        checkStringFormatting(expression)
        checkCaseConversion(expression)
    }

    private fun checkStringFormatting(expression: KtQualifiedExpression) {
        if (expression.receiverExpression.text == "String" &&
            expression.getCalleeExpressionIfAny()?.text == "format" &&
            expression.containsStringTemplate()
        ) {
            report(
                CodeSmell(
                issue, Entity.from(expression),
                "${expression.text} uses implicitly default locale for string formatting.")
            )
        }
    }

    private fun checkCaseConversion(expression: KtQualifiedExpression) {
        if (isStringOrNullableString(expression.receiverExpression.getType(bindingContext)) &&
            expression.isCalleeCaseConversion() &&
            expression.isCalleeNoArgs()) {
            report(CodeSmell(
                issue,
                Entity.from(expression),
                "${expression.text} uses implicitly default locale for case conversion."))
        }
    }
}

private fun KtQualifiedExpression.isCalleeCaseConversion(): Boolean {
    return getCalleeExpressionIfAny()?.text in arrayOf("toLowerCase", "toUpperCase")
}

private fun KtQualifiedExpression.isCalleeNoArgs(): Boolean {
    val lastCallExpression = lastChild as? KtCallExpression
    return lastCallExpression?.valueArguments.isNullOrEmpty()
}

private fun KtQualifiedExpression.containsStringTemplate(): Boolean {
    val lastCallExpression = lastChild as? KtCallExpression
    return lastCallExpression?.valueArguments
        ?.firstOrNull()
        ?.run { children.firstOrNull() } is KtStringTemplateExpression
}
