package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
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
class NullableToStringCall(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "This call may return the string \"null\"",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        val qualified = expression.getQualifiedExpressionForReceiver()
        when {
            qualified != null -> {
                if (qualified.descriptor()?.fqNameOrNull() != FqName("kotlin.toString")) return
            }
            expression.parent is KtStringTemplateEntry -> {
                val compilerResources = compilerResources ?: return
                val descriptor = expression.descriptor() ?: return
                val originalType = descriptor.returnType ?.takeIf { it.isNullable() } ?: return
                val dataFlowInfo =
                    bindingContext[BindingContext.EXPRESSION_TYPE_INFO, expression]?.dataFlowInfo ?: return
                val dataFlowValue = compilerResources.dataFlowValueFactory.createDataFlowValue(
                    expression, originalType, bindingContext, descriptor
                )
                val dataFlowTypes =
                    dataFlowInfo.getStableTypes(dataFlowValue, compilerResources.languageVersionSettings)
                if (dataFlowTypes.any { !it.isNullable() }) return
            }
            else -> return
        }

        val targetExpression = qualified ?: expression.parent
        val codeSmell = CodeSmell(
            issue,
            Entity.from(targetExpression),
            "This call '${targetExpression.text}' may return the string \"null\"."
        )
        report(codeSmell)
    }

    private fun KtExpression.descriptor(): CallableDescriptor? = getResolvedCall(bindingContext)?.resultingDescriptor
}
