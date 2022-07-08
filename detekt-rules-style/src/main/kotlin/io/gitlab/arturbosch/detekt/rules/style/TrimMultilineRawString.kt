package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis

/**
 * All the Raw strings that have more than one line should be followed by `trimMargin()` or `trimIndent()`.
 *
 * <noncompliant>
 * """
 *   Hello World!
 *   How are you?
 * """
 * </noncompliant>
 *
 * <compliant>
 * """
 *   |  Hello World!
 *   |  How are you?
 * """.trimMargin()
 *
 * """
 *   Hello World!
 *   How are you?
 * """.trimIndent()
 *
 * """Hello World! How are you?"""
 * </compliant>
 */
class TrimMultilineRawString(val config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Multiline raw strings should be followed by `trimMargin()` or `trimIndent()`.",
        Debt.FIVE_MINS
    )

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)

        if (expression.text.lines().count() == 1) return

        val nextCall = expression.getQualifiedExpressionForSelectorOrThis()
            .getQualifiedExpressionForReceiver()
            ?.selectorExpression
            ?.asKtCallExpression()
            ?.calleeExpression
            ?.text

        if (nextCall !in trimFunctions) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Multiline raw strings should be followed by `trimMargin()` or `trimIndent()`",
                )
            )
        }
    }
}

private fun KtExpression.asKtCallExpression(): KtCallExpression? = this as? KtCallExpression

private val trimFunctions = listOf("trimIndent", "trimMargin")
