package dev.detekt.rules.emptyblocks

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * Reports empty `while` expressions. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyWhileBlock(config: Config) : EmptyRule(config) {

    override fun visitWhileExpression(expression: KtWhileExpression) {
        super.visitWhileExpression(expression)
        expression.body?.addFindingIfBlockExprIsEmpty()
    }
}
