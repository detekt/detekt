package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isNullable
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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
@RequiresTypeResolution
class NullableToStringCall(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "`toString()` on nullable receiver may return the string \"null\"",
        Debt.FIVE_MINS
    )

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)

        val simpleOrCallExpression = expression.parent.safeAs<KtCallExpression>() ?: expression
        val targetExpression = simpleOrCallExpression.targetExpression() ?: return

        if (simpleOrCallExpression.safeAs<KtCallExpression>()?.calleeExpression?.text == "toString" &&
            simpleOrCallExpression.descriptor()?.fqNameOrNull() == toString
        ) {
            report(targetExpression)
        } else if (targetExpression.parent is KtStringTemplateEntry) {
            compilerResources?.let { compilerResources ->
                if (targetExpression.isNullable(
                        bindingContext,
                        compilerResources.languageVersionSettings,
                        compilerResources.dataFlowValueFactory,
                        shouldConsiderPlatformTypeAsNullable = false,
                    )
                ) {
                    report(targetExpression.parent)
                }
            }
        }
    }

    @Suppress("ReturnCount")
    private fun KtExpression.targetExpression(): KtExpression? {
        val qualifiedExpression = getStrictParentOfType<KtQualifiedExpression>()
        val targetExpression = if (qualifiedExpression != null) {
            qualifiedExpression.takeIf { it.selectorExpression == this } ?: return null
        } else {
            this
        }
        if (targetExpression.getStrictParentOfType<KtQualifiedExpression>() != null) return null
        return targetExpression
    }

    private fun report(element: PsiElement) {
        val codeSmell = CodeSmell(
            issue,
            Entity.from(element),
            "This call '${element.text}' may return the string \"null\"."
        )
        report(codeSmell)
    }

    private fun KtExpression.descriptor(): CallableDescriptor? = getResolvedCall(bindingContext)?.resultingDescriptor

    companion object {
        val toString = FqName("kotlin.toString")
    }
}
