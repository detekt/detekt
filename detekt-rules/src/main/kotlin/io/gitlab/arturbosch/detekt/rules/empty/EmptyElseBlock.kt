package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyElseBlock(config: Config) : EmptyRule(config) {

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        expression.`else`?.addFindingIfBlockExprIsEmpty()
    }
}
