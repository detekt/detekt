package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyElseBlock(config: Config) : EmptyRule(config) {
    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        expression.`else`?.addFindingIfBlockExprIsEmpty() ?: checkThenBodyForLoneSemicolon(expression) {
            report(
                Finding(
                    Entity.from(it),
                    "This else block is empty and can be removed."
                )
            )
        }
    }
}
