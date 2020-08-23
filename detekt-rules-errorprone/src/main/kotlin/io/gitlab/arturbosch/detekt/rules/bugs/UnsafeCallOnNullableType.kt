package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.types.typeUtil.TypeNullability
import org.jetbrains.kotlin.types.typeUtil.nullability

/**
 * Reports unsafe calls on nullable types. These calls will throw a NullPointerException in case
 * the nullable value is null. Kotlin provides many ways to work with nullable types to increase
 * null safety. Guard the code appropriately to prevent NullPointerExceptions.
 *
 * <noncompliant>
 * fun foo(str: String?) {
 *     println(str!!.length)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(str: String?) {
 *     println(str?.length)
 * }
 * </compliant>
 *
 * @active since v1.2.0
 * @requiresTypeResolution
 */
class UnsafeCallOnNullableType(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("UnsafeCallOnNullableType",
            Severity.Defect,
            "It will throw a NullPointerException at runtime if your nullable value is null.",
            Debt.TWENTY_MINS)

    @Suppress("ReturnCount")
    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return
        val type = expression.baseExpression?.getType(bindingContext) ?: return
        if (type.nullability() != TypeNullability.NULLABLE) return

        if (expression.operationToken == KtTokens.EXCLEXCL) {
            report(CodeSmell(issue, Entity.from(expression), "Calling !! on a nullable type will throw a " +
                    "NullPointerException at runtime in case the value is null. It should be avoided."))
        }
    }
}
