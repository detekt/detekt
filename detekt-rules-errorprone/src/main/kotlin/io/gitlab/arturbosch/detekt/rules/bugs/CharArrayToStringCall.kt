package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.resolve.calls.util.getType

/**
 * Reports `CharArray.toString()` calls that do not return the expected result.
 *
 * <noncompliant>
 * val s = ""
 * val charArray = "hello😅".toCharArray()
 *
 * println("$s$charArray") // [C@4f023edb
 * println(charArray.toString()) // [C@4f023edb
 * println(s + charArray) // [C@4f023edb
 * </noncompliant>
 *
 * <compliant>
 * println("$s${charArray.concatToString()}") // hello😅
 * println(charArray.concatToString()) // hello😅
 * println(s + charArray.concatToString()) // hello😅
 * </compliant>
 */
class CharArrayToStringCall(config: Config) :
    Rule(
        config,
        "`CharArray.toString()` call does not return expected result."
    ),
    RequiresTypeResolution {
    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)

        val selector = expression.selectorExpression as? KtCallExpression ?: return
        if (selector.isCharArrayToString()) {
            report(expression)
        }
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val right = expression.right ?: return
        if (right.isCharArray() && expression.isString()) {
            report(right)
        }
    }

    override fun visitStringTemplateEntry(entry: KtStringTemplateEntry) {
        super.visitStringTemplateEntry(entry)

        val expression = entry.expression ?: return
        if (expression.isCharArray()) {
            report(expression)
        }
    }

    private fun KtCallExpression.isCharArrayToString() = isCalling(FqName("kotlin.CharArray.toString"), bindingContext)

    private fun KtExpression.isCharArray() = getType(bindingContext)?.fqNameOrNull() == FqName("kotlin.CharArray")

    private fun KtBinaryExpression.isString() = getType(bindingContext)?.fqNameOrNull() == FqName("kotlin.String")

    private fun report(expression: KtExpression) {
        val codeSmell = CodeSmell(
            Entity.from(expression),
            "Use `concatToString()` call instead of `toString()` call."
        )
        report(codeSmell)
    }
}
