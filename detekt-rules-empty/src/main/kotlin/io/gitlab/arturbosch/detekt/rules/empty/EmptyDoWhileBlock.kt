package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtDoWhileExpression

/**
 * Reports empty `do`/`while` loops. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyDoWhileBlock(config: Config) : EmptyRule(config) {

    override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
        super.visitDoWhileExpression(expression)
        expression.body?.addFindingIfBlockExprIsEmpty()
    }
}
