package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
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
