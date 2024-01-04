package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isNullable
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.resolve.BindingContext
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
 *     val x = checkNotNull(bar) as String
 * }
 *
 * // Alternative
 * fun foo(bar: Any?) {
 *     val x = (bar ?: error("null assertion message")) as String
 * }
 * </compliant>
 */
@RequiresTypeResolution
class CastNullableToNonNullableType(config: Config) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        "Nullable type to non-null type cast is found. Consider using two assertions, " +
            "`null` assertions and type cast",
    )

    @Configuration("Whether platform types should be considered as non-nullable and ignored by this rule")
    private val ignorePlatformTypes: Boolean by config(true)

    @Suppress("ReturnCount")
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_KEYWORD) return
        if (expression.left.text == KtTokens.NULL_KEYWORD.value) return
        val typeRef = expression.right ?: return
        val simpleType = bindingContext[BindingContext.TYPE, typeRef] ?: return
        simpleType.isNullable().ifTrue { return }
        val compilerResourcesNonNull = compilerResources ?: return
        expression.left.isNullable(
            bindingContext,
            compilerResourcesNonNull.languageVersionSettings,
            compilerResourcesNonNull.dataFlowValueFactory,
            shouldConsiderPlatformTypeAsNullable = ignorePlatformTypes.not(),
        ).ifFalse { return }

        val message =
            "Use separate `null` assertion and type cast like ('(${expression.left.text} ?: " +
                "error(\"null assertion message\")) as ${typeRef.text}') instead of '${expression.text}'."
        report(CodeSmell(issue, Entity.from(operationReference), message))
    }
}
