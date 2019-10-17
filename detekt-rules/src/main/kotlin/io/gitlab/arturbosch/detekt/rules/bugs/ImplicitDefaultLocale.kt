package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny

/**
 * Prefer passing [java.util.Locale] explicitly than using implicit default value when formatting
 * strings.
 *
 * The default locale is almost always not appropriate for machine-readable text like HTTP headers.
 * For example if locale with tag `ar-SA-u-nu-arab` is a current default then `%d` placeholders
 * will be evaluated to numbers consisting of Eastern-Arabic (non-ASCII) digits.
 * [java.util.Locale.US] is recommended for machine-readable output.
 *
 * <noncompliant>
 * String.format("Timestamp: %d", System.currentTimeMillis())
 * </noncompliant>
 *
 * <compliant>
 * String.format(Locale.US, "Timestamp: %d", System.currentTimeMillis())
 * </compliant>
 */
class ImplicitDefaultLocale(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
            "ImplicitDefaultLocale", Severity.CodeSmell,
            "Implicit default locale used for string processing. Consider using explicit locale.",
            Debt.FIVE_MINS
    )

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        if (expression.receiverExpression.text == "String" &&
            expression.getCalleeExpressionIfAny()?.text == "format" &&
            expression.containsStringTemplate()
        ) {
            report(CodeSmell(
                issue, Entity.from(expression),
                "${expression.text} uses implicitly default locale for string formatting."))
        }
        super.visitDotQualifiedExpression(expression)
    }
}

private fun KtDotQualifiedExpression.containsStringTemplate(): Boolean {
    val lastCallExpression = lastChild as? KtCallExpression
    return lastCallExpression?.valueArgumentList
        ?.arguments
        ?.firstOrNull()
        ?.children
        ?.firstOrNull() is KtStringTemplateExpression
}
