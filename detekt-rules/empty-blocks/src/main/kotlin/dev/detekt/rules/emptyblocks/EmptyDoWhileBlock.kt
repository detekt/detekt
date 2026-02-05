package dev.detekt.rules.emptyblocks

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import org.jetbrains.kotlin.psi.KtDoWhileExpression

/**
 * Reports empty `do`/`while` loops. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyDoWhileBlock(config: Config) : EmptyRule(config) {

    override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
        super.visitDoWhileExpression(expression)
        expression.body?.addFindingIfBlockExprIsEmpty()
    }
}
