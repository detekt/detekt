package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypesAndPredicate
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * This rule reports when the string can be converted to Kotlin raw string.
 * Usage of a raw string is preferred as that avoids the need for escaping strings escape characters like \n, \t, ".
 * Raw string also allows us to represent multiline string without the need of \n.
 * Also, see [Kotlin coding convention](https://kotlinlang.org/docs/coding-conventions.html#strings)  for
 * recommendation on using multiline strings
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
 * """.trimIndent()
 *
 * val patRegex = """/^(\/[^\/]+){0,2}\/?$/gm"""
 * </compliant>
 */
class StringShouldBeRawString(config: Config) : Rule(config, "The string can be converted to raw string.") {

    @Configuration("maximum escape characters allowed")
    private val maxEscapedCharacterCount by config(2)

    @Configuration("list of characters to ignore")
    private val ignoredCharacters by config(emptyList<String>())

    private val KtElement.leftMostElementOfLeftSubtree: KtElement
        get() {
            val leftChild = (this as? KtBinaryExpression)?.left?.let { KtPsiUtil.safeDeparenthesize(it) } ?: return this
            return leftChild.leftMostElementOfLeftSubtree
        }

    private val KtElement.rightMostElementOfRightSubtree: KtElement
        get() {
            val leftChild =
                (this as? KtBinaryExpression)?.right?.let { KtPsiUtil.safeDeparenthesize(it) } ?: return this
            return leftChild.rightMostElementOfRightSubtree
        }

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)

        val callExpression = expression.getParentOfType<KtCallExpression>(strict = true)

        if (callExpression?.calleeExpression?.text in listOfAllowedMethod) {
            return
        }

        if (expression.text.matches(regexForOnlyQuotes)) {
            return
        }

        val expressionParent = expression.parents.firstOrNull { it !is KtParenthesizedExpression }
        val rootElement = expression.getRootExpression()
        if (
            expressionParent !is KtBinaryExpression ||
            (rootElement != null && expression.isPivotElementInTheTree(rootElement))
        ) {
            val stringSeqToProcess = rootElement?.getStringSequenceExcludingRawString() ?: sequenceOf("")
            val hasNoViolations = stringSeqToProcess.flatMap { stringTemplateExpressionText ->
                REGEX_FOR_ESCAPE_CHARS.findAll(stringTemplateExpressionText).filter {
                    it.value !in ignoredCharacters
                }
            }.drop(maxEscapedCharacterCount).none()
            if (hasNoViolations.not()) {
                report(
                    Finding(
                        Entity.from(rootElement ?: expression),
                        "String with escape characters should be converted to raw string",
                    )
                )
            }
        }
    }

    private fun KtStringTemplateExpression.isPivotElementInTheTree(rootElement: KtElement): Boolean {
        val leftMostElementOfLeftSubtree = rootElement.leftMostElementOfLeftSubtree
        return this == if (leftMostElementOfLeftSubtree is KtStringTemplateExpression) {
            leftMostElementOfLeftSubtree
        } else {
            rootElement.rightMostElementOfRightSubtree
        }
    }

    private fun KtElement.getStringSequenceExcludingRawString(): Sequence<String> {
        fun KtElement.getStringSequence(): Sequence<KtStringTemplateExpression> =
            sequence {
                if (this@getStringSequence is KtStringTemplateExpression) {
                    yield(this@getStringSequence)
                } else if (this@getStringSequence is KtBinaryExpression) {
                    left?.let {
                        yieldAll(KtPsiUtil.safeDeparenthesize(it).getStringSequence())
                    }
                    right?.let {
                        yieldAll(KtPsiUtil.safeDeparenthesize(it).getStringSequence())
                    }
                }
            }
        return this.getStringSequence().filter {
            (it.text.startsWith("\"\"\"") && it.text.endsWith("\"\"\"")).not()
        }.map {
            it.text
        }
    }

    private fun KtElement.getRootExpression(): KtElement? =
        this.getParentOfTypesAndPredicate(
            false,
            KtBinaryExpression::class.java,
            KtParenthesizedExpression::class.java,
            KtStringTemplateExpression::class.java,
        ) {
            val parent = (it as KtExpression).parent
            parent !is KtBinaryExpression && parent !is KtParenthesizedExpression
        }?.let { KtPsiUtil.safeDeparenthesize(it) }

    companion object {
        private val REGEX_FOR_ESCAPE_CHARS = """\\[t"\\n]""".toRegex()
        private val listOfAllowedMethod = listOf(
            "replaceIndent",
            "prependIndent",
        )
        private val regexForOnlyQuotes = """"(?:\\")*"""".toRegex()
    }
}
