package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable

/**
 * Turn on this rule to flag 'toString' calls with a nullable receiver that may return the string "null".
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
 *
 * @since 1.11.0
 * @requiresTypeResolution
 */
@Suppress("ReturnCount")
class NullableToStringCall(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "This call may return the string \"null\"",
        Debt.FIVE_MINS
    )

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val simpleOrCallExpression = expression.parent.safeAs<KtCallExpression>() ?: expression
        val targetExpression = simpleOrCallExpression.targetExpression() ?: return

        if (simpleOrCallExpression.safeAs<KtCallExpression>()?.calleeExpression?.text == "toString" &&
            simpleOrCallExpression.descriptor()?.fqNameOrNull() == toString) {
            report(targetExpression)
        } else if (targetExpression.parent is KtStringTemplateEntry && targetExpression.isNullable()) {
            report(targetExpression.parent)
        }
    }

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

    private fun KtExpression.isNullable(): Boolean {
        val safeAccessOperation = safeAs<KtSafeQualifiedExpression>()?.operationTokenNode?.safeAs<PsiElement>()
        if (safeAccessOperation != null) {
            return bindingContext.diagnostics.forElement(safeAccessOperation).none {
                it.factory == Errors.UNNECESSARY_SAFE_CALL
            }
        }
        val compilerResources = compilerResources ?: return false
        val descriptor = descriptor() ?: return false
        val originalType = descriptor.returnType ?.takeIf { it.isNullable() } ?: return false
        val dataFlowInfo =
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, this]?.dataFlowInfo ?: return false
        val dataFlowValue =
            compilerResources.dataFlowValueFactory.createDataFlowValue(this, originalType, bindingContext, descriptor)
        val dataFlowTypes =
            dataFlowInfo.getStableTypes(dataFlowValue, compilerResources.languageVersionSettings)
        return dataFlowTypes.all { it.isNullable() }
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
