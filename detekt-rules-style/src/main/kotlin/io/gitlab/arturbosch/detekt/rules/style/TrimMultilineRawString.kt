package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver

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

        if (expression.isRawStringWithLineBreak() && !expression.isTrimmed()) {
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

fun KtStringTemplateExpression.isRawStringWithLineBreak(): Boolean =
    text.startsWith("\"\"\"") && text.endsWith("\"\"\"") && entries.any {
        val literalText = it.safeAs<KtLiteralStringTemplateEntry>()?.text
        literalText != null && "\n" in literalText
    }

fun KtStringTemplateExpression.isTrimmed(): Boolean {
    val nextCall = getQualifiedExpressionForReceiver()
        ?.selectorExpression
        ?.safeAs<KtCallExpression>()
        ?.calleeExpression
        ?.text

    return nextCall in trimFunctions
}

private val trimFunctions = listOf("trimIndent", "trimMargin")
