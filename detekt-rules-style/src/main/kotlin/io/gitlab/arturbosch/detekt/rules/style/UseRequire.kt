package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.arguments
import io.gitlab.arturbosch.detekt.rules.isEmptyOrSingleStringArgument
import io.gitlab.arturbosch.detekt.rules.isEnclosedByConditionalStatement
import io.gitlab.arturbosch.detekt.rules.isIllegalArgumentException
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * Kotlin provides a much more concise way to check preconditions than to manually throw an
 * IllegalArgumentException.
 *
 * <noncompliant>
 * if (value == null) throw IllegalArgumentException("value should not be null")
 * if (value < 0) throw IllegalArgumentException("value is $value but should be at least 0")
 * </noncompliant>
 *
 * <compliant>
 * requireNotNull(value) { "value should not be null" }
 * require(value >= 0) { "value is $value but should be at least 0" }
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class UseRequire(config: Config) :
    Rule(
        config,
        "Use require() instead of throwing an IllegalArgumentException."
    ),
    RequiresTypeResolution {
    override fun visitThrowExpression(expression: KtThrowExpression) {
        if (!expression.isIllegalArgumentException()) return
        if (expression.hasMoreExpressionsInBlock()) return

        if (expression.isEnclosedByConditionalStatement() &&
            expression.arguments.isEmptyOrSingleStringArgument(bindingContext)
        ) {
            report(CodeSmell(Entity.from(expression), description))
        }
    }

    private fun KtThrowExpression.hasMoreExpressionsInBlock(): Boolean =
        (parent as? KtBlockExpression)?.run { statements.size > 1 } ?: false
}
