package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.resolve.calls.util.getType
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
 */
@ActiveByDefault(since = "1.2.0")
class UnsafeCallOnNullableType(config: Config) :
    Rule(
        config,
        "Unsafe calls on nullable types detected. These calls will throw a NullPointerException in case " +
            "the nullable value is null."
    ),
    RequiresTypeResolution {
    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)
        if (expression.operationToken == KtTokens.EXCLEXCL &&
            expression.baseExpression?.getType(bindingContext)?.nullability() == TypeNullability.NULLABLE
        ) {
            report(
                CodeSmell(
                    Entity.from(expression),
                    "Calling !! on a nullable type will throw a " +
                        "NullPointerException at runtime in case the value is null. It should be avoided."
                )
            )
        }
    }
}
