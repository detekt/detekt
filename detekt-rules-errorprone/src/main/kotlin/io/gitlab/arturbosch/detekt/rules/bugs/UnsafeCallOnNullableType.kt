package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPostfixExpression

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
    RequiresAnalysisApi {

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)

        if (expression.operationToken != KtTokens.EXCLEXCL) return

        val isNullable = analyze(expression) {
            expression.baseExpression?.expressionType?.nullability?.isNullable == true
        }

        if (isNullable) {
            report(
                Finding(
                    Entity.from(expression),
                    "Calling !! on a nullable type will throw a " +
                        "NullPointerException at runtime in case the value is null. It should be avoided."
                )
            )
        }
    }
}
