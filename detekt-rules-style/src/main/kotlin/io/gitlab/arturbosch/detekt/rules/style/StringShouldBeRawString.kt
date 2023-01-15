package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypesAndPredicate
import org.jetbrains.kotlin.psi2ir.deparenthesize

/**
 * This rule reports when string can be converted to Kotlin raw string.
 *
 * Usage of raw string is preferred as that avoids the need of escaping strings escape characters like `\n`, `\t`, `"`.
 * Raw string also allow us to represent multiline string without the need of `\n`
 *
 * <noncompliant>
 * val windowJson = "{\n" +
 *                  "  \"window\": {\n" +
 *                  "    \"title\": \"Sample Quantum With AI and ML Widget\",\n" +
 *                  "    \"name\": \"main_window\",\n" +
 *                  "    \"width\": 500,\n" +
 *                  "    \"height\": 500\n" +
 *                  "  }\n" +
 *                  "}"
 *
 * val patRegex = "/^(\\/[^\\/]+){0,2}\\/?\$/gm\n"
 * </noncompliant>
 *
 * <compliant>
 * val windowJson = """
 *     {
 *          "window": {
 *              "title": "Sample Quantum With AI and ML Widget",
 *              "name": "main_window",
 *              "width": 500,
 *              "height": 500
 *          }
 *     }
 * """.trimMargin()
 *
 * val patRegex = """/^(\/[^\/]+){0,2}\/?$/gm"""
 * </compliant>
 */
class StringShouldBeRawString(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "The string can be converted to raw string.",
        Debt.FIVE_MINS,
    )

    @Configuration("maximum escape characters allowed")
    private val maxEscapedCharacterCount by config(2)

    @Configuration("list of characters to ignore")
    private val ignoredCharacters by config(emptyList<String>())

    private val KtElement.leftMostElementOfLeftSubtree: KtElement
        get() {
            val leftChild = (this as? KtBinaryExpression)?.left?.deparenthesize() ?: return this
            return leftChild.leftMostElementOfLeftSubtree
        }

    private val KtElement.rightMostElementOfRightSubtree: KtElement
        get() {
            val leftChild = (this as? KtBinaryExpression)?.right?.deparenthesize() ?: return this
            return leftChild.rightMostElementOfRightSubtree
        }

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)
        if (maxEscapedCharacterCount == Int.MAX_VALUE) {
            return
        }
        val maxEscapedCharacterCount = this.maxEscapedCharacterCount.coerceAtLeast(0)
        val expressionParent = expression.getParentExpressionAfterParenthesis()
        val rootElement = expression.getRootExpression()
        if (
            expressionParent !is KtBinaryExpression ||
            (rootElement != null && expression.isPivotElementInTheTree(rootElement))
        ) {
            val shouldReport = rootElement.buildStringExcludingRawString().flatMap { stringTemplateExpressionText ->
                REGEX_FOR_ESCAPE_CHARS.findAll(stringTemplateExpressionText).filter {
                    it.value !in ignoredCharacters
                }
            }.take(maxEscapedCharacterCount + 1).toList().size > maxEscapedCharacterCount
            if (shouldReport) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(rootElement ?: expression),
                        "String with escape characters should be converted to raw string",
                    )
                )
            }
        }
    }

    private fun KtStringTemplateExpression.isPivotElementInTheTree(
        rootElement: KtElement,
    ): Boolean {
        val leftMostElementOfLeftSubtree = rootElement.leftMostElementOfLeftSubtree
        return this == if (leftMostElementOfLeftSubtree is KtStringTemplateExpression) {
            leftMostElementOfLeftSubtree
        } else {
            rootElement.rightMostElementOfRightSubtree
        }
    }

    private fun KtElement?.buildStringExcludingRawString(): Sequence<String> {
        this ?: return sequence { yield("") }

        fun KtElement?.getStringSequence(): Sequence<KtStringTemplateExpression> = sequence {
            if (this@getStringSequence is KtStringTemplateExpression) {
                yield(this@getStringSequence)
            } else if (this@getStringSequence is KtBinaryExpression) {
                yieldAll(left?.deparenthesize().getStringSequence())
                yieldAll(right?.deparenthesize().getStringSequence())
            }
        }

        return this.getStringSequence().filter {
            (it.text.startsWith("\"\"\"") && it.text.endsWith("\"\"\"")).not()
        }.map {
            it.text
        }
    }

    private fun KtExpression.getParentExpressionAfterParenthesis(): PsiElement? =
        this.getParentOfTypesAndPredicate(true, PsiElement::class.java) { it !is KtParenthesizedExpression }

    private fun KtElement.getRootExpression(): KtElement? {
        return this.getParentOfTypesAndPredicate(
            false,
            KtBinaryExpression::class.java,
            KtParenthesizedExpression::class.java,
            KtStringTemplateExpression::class.java,
        ) {
            val parent = (it as KtExpression).parent
            parent !is KtBinaryExpression && parent !is KtParenthesizedExpression
        }?.deparenthesize()
    }

    companion object {
        private val REGEX_FOR_ESCAPE_CHARS = """\\[t"\\n]""".toRegex()
    }
}
