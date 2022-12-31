package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.getDataFlowAwareTypes
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.utils.addToStdlib.ifFalse
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

/**
 * Reports cast of nullable variable to non-null type. Cast like this can hide `null`
 * problems in your code. The compliant code would be that which will correctly check
 *  for two things (nullability and type) and not just one (cast).
 *
 * <noncompliant>
 * fun foo(bar: Any?) {
 *     val x = bar as String
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(bar: Any?) {
 *     val x = (bar ?: error("null assertion message")) as String
 * }
 * </compliant>
 */
@RequiresTypeResolution
@Suppress("ReturnCount")
class CastNullableToNonNullableType(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Nullable type to non-null type cast is found. Consider using two assertions, " +
            "`null` assertions and type cast",
        Debt.FIVE_MINS
    )

    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_KEYWORD) return
        if (expression.left.text == KtTokens.NULL_KEYWORD.value) return
        val typeElement = expression.right?.typeElement ?: return
        (typeElement is KtNullableType).ifTrue { return }
        expression.left.isNullable().ifFalse { return }

        val message =
            "Use separate `null` assertion and type cast like ('(${expression.left.text} ?: " +
                "error(\"null assertion message\")) as ${typeElement.text}') instead of '${expression.text}'."
        report(CodeSmell(issue, Entity.from(operationReference), message))
    }

    private fun KtExpression.isNullable(): Boolean {
        val compilerResources = compilerResources ?: return false

        val safeAccessOperation = safeAs<KtSafeQualifiedExpression>()?.operationTokenNode?.safeAs<PsiElement>()
        if (safeAccessOperation != null) {
            return bindingContext.diagnostics.forElement(safeAccessOperation).none {
                it.factory == Errors.UNNECESSARY_SAFE_CALL
            }
        }
        val originalType = descriptor()?.returnType?.takeIf { it.isNullable() } ?: return false
        val dataFlowTypes = getDataFlowAwareTypes(
            bindingContext,
            compilerResources.languageVersionSettings,
            compilerResources.dataFlowValueFactory,
            originalType
        )
        return dataFlowTypes.all { it.isNullable() }
    }

    private fun KtExpression.descriptor(): CallableDescriptor? = getResolvedCall(bindingContext)?.resultingDescriptor
}
