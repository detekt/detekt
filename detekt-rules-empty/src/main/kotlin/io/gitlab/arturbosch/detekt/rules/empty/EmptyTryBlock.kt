package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtTryExpression

/**
 * Reports empty `try` blocks. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.6.0")
class EmptyTryBlock(config: Config) : EmptyRule(config) {

    override fun visitTryExpression(expression: KtTryExpression) {
        super.visitTryExpression(expression)
        expression.tryBlock.addFindingIfBlockExprIsEmpty()
    }
}
