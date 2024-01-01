package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isConstant
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

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
class TrimMultilineRawString(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        "Multiline raw strings should be followed by `trimMargin()` or `trimIndent()`.",
    )

    @Configuration("allows to provide a list of multiline string trimming methods")
    private val trimmingMethods: List<String> by config(listOf("trimIndent", "trimMargin"))

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)

        if (expression.isRawStringWithLineBreak() &&
            !expression.isTrimmed(trimmingMethods) &&
            !expression.isExpectedAsConstant()
        ) {
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
    text.startsWith("\"\"\"") &&
        text.endsWith("\"\"\"") &&
        entries.any {
            val literalText = (it as? KtLiteralStringTemplateEntry)?.text
            literalText != null && "\n" in literalText
        }

fun KtStringTemplateExpression.isTrimmed(trimmingMethods: List<String>): Boolean {
    val nextCall = getQualifiedExpressionForReceiver()
        ?.selectorExpression
        ?.let { it as? KtCallExpression }
        ?.calleeExpression
        ?.text

    return nextCall in trimmingMethods
}

@Suppress("ReturnCount")
private fun KtStringTemplateExpression.isExpectedAsConstant(): Boolean {
    val expression = KtPsiUtil.safeDeparenthesize(this)

    val property = getStrictParentOfType<KtProperty>()?.takeIf { it.initializer == expression }
    if (property != null && property.isConstant()) return true

    val argument = expression.getStrictParentOfType<KtValueArgument>()
        ?.takeIf { it.getArgumentExpression() == expression }
    if (argument?.parent?.parent is KtAnnotationEntry) return true

    val parameter = expression.getStrictParentOfType<KtParameter>()
        ?.takeIf { it.defaultValue == expression }
    val primaryConstructor = parameter?.parent?.parent as? KtPrimaryConstructor
    if (primaryConstructor?.containingClass()?.isAnnotation() == true) return true

    return false
}
