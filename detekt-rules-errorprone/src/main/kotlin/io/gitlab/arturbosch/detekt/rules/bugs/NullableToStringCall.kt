package io.gitlab.arturbosch.detekt.rules.bugs

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isNullable
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.BASE_KOTLIN_PACKAGE
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector

/**
 * Reports `toString()` calls with a nullable receiver that may return the string "null".
 *
 * <noncompliant>
 * fun foo(a: Any?): String {
 *     return a.toString()
 * }
 *
 * fun bar(a: Any?): String {
 *     return "$a"
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(a: Any?): String {
 *     return a?.toString() ?: "-"
 * }
 *
 * fun bar(a: Any?): String {
 *     return "${a ?: "-"}"
 * }
 * </compliant>
 */
class NullableToStringCall(config: Config) :
    Rule(
        config,
        "`toString()` on nullable receiver may return the string \"null\""
    ),
    RequiresAnalysisApi {

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)

        val callExpression = expression.parent as? KtCallExpression
        val qualifiedExpression = (callExpression ?: expression).getQualifiedExpressionForSelector()
        val stringTemplateEntry = (qualifiedExpression ?: callExpression ?: expression).parent as? KtStringTemplateEntry

        when {
            callExpression?.calls(toString) == true -> report(qualifiedExpression ?: callExpression)
            stringTemplateEntry?.hasNullableExpression() == true -> report(stringTemplateEntry)
        }
    }

    private fun KtCallExpression.calls(callableId: CallableId): Boolean {
        analyze(this) {
            return resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId == callableId
        }
    }

    private fun KtStringTemplateEntry.hasNullableExpression(): Boolean {
        val expression = this.expression ?: return false
        return expression.isNullable(shouldConsiderPlatformTypeAsNullable = false)
    }

    private fun report(element: PsiElement) {
        val finding = Finding(
            Entity.from(element),
            "This call '${element.text}' may return the string \"null\"."
        )
        report(finding)
    }

    companion object {
        val toString = CallableId(BASE_KOTLIN_PACKAGE, Name.identifier("toString"))
    }
}
