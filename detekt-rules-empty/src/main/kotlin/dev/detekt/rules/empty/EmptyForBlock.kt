package dev.detekt.rules.empty

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * Reports empty `for` loops. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyForBlock(config: Config) : EmptyRule(config) {

    override fun visitForExpression(expression: KtForExpression) {
        super.visitForExpression(expression)
        expression.body?.addFindingIfBlockExprIsEmpty()
    }
}
