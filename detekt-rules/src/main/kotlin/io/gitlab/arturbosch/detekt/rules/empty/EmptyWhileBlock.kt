package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * Reports empty `while` expressions. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyWhileBlock(config: Config) : EmptyRule(config) {

    override fun visitWhileExpression(expression: KtWhileExpression) {
        super.visitWhileExpression(expression)
        expression.body?.addFindingIfBlockExprIsEmpty()
    }
}
