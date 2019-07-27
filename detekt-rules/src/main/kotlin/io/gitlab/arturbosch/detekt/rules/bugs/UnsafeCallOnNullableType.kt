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
 */
class UnsafeCallOnNullableType(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("UnsafeCallOnNullableType",
            Severity.Defect,
            "It will throw a NullPointerException at runtime if your nullable value is null.",
            Debt.TWENTY_MINS)

    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)
        if (expression.operationToken == KtTokens.EXCLEXCL) {
            report(CodeSmell(issue, Entity.from(expression), "Calling !! on a nullable type will throw a " +
                    "NullPointerException at runtime in case the value is null. It should be avoided."))
        }
    }
}
