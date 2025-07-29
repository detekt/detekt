package io.gitlab.arturbosch.detekt.rules.empty

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `if` blocks. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyIfBlock(config: Config) : EmptyRule(config) {
    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        expression.then?.addFindingIfBlockExprIsEmpty() ?: checkThenBodyForLoneSemicolon(expression) {
            report(
                Finding(
                    Entity.from(it),
                    "This if block is empty and can be removed."
                )
            )
        }
    }
}
