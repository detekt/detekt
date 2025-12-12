package dev.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry

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
@ActiveByDefault(since = "2.0.0")
class CharArrayToStringCall(config: Config) :
    Rule(
        config,
        "`CharArray.toString()` call does not return expected result."
    ),
    RequiresAnalysisApi {

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)

        val selector = expression.selectorExpression as? KtCallExpression ?: return
        analyze(expression) {
            if (isCharArray(expression.receiverExpression) && isToStringCall(selector)) {
                report(expression)
            }
        }
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val right = expression.right ?: return
        analyze(expression) {
            if (isCharArray(right) && isString(expression)) {
                report(right)
            }
        }
    }

    override fun visitStringTemplateEntry(entry: KtStringTemplateEntry) {
        super.visitStringTemplateEntry(entry)

        val expression = entry.expression ?: return
        analyze(expression) {
            if (isCharArray(expression)) {
                report(expression)
            }
        }
    }

    private fun KaSession.isToStringCall(expression: KtExpression) =
        expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId == toStringCallableId

    private fun KaSession.isCharArray(expression: KtExpression) = classId(expression) == charArrayClassId

    private fun KaSession.isString(expression: KtExpression) = classId(expression) == stringClassId

    private fun KaSession.classId(expression: KtExpression) = expression.expressionType?.symbol?.classId

    private fun report(expression: KtExpression) {
        val finding = Finding(
            Entity.from(expression),
            "Use `concatToString()` call instead of `toString()` call."
        )
        report(finding)
    }

    companion object {
        private val toStringCallableId = CallableId(StandardClassIds.Any, Name.identifier("toString"))
        private val stringClassId = StandardClassIds.String
        private val charArrayClassId = ClassId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("CharArray"))
    }
}
