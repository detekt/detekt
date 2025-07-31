package dev.detekt.rules.performance

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Avoid temporary objects when converting primitive types to String. This has a performance penalty when compared
 * to using primitive types directly.
 * To solve this issue, remove the wrapping type.
 *
 * <noncompliant>
 * val i = Integer(1).toString() // temporary Integer instantiation just for the conversion
 * </noncompliant>
 *
 * <compliant>
 * val i = Integer.toString(1)
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class UnnecessaryTemporaryInstantiation(config: Config) : Rule(
    config,
    "Avoid temporary objects when converting primitive types to `String`."
) {

    private val types: Set<String> = hashSetOf("Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double")

    override fun visitCallExpression(expression: KtCallExpression) {
        if (isPrimitiveWrapperType(expression.calleeExpression) &&
            expression.nextSibling?.nextSibling?.text == "toString()"
        ) {
            report(Finding(Entity.from(expression), description))
        }
    }

    private fun isPrimitiveWrapperType(expression: KtExpression?) = types.contains(expression?.text)
}
