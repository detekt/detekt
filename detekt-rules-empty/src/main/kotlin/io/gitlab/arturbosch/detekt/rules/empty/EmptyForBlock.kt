package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * Reports empty `for` loops. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault("v1.0.0")
class EmptyForBlock(config: Config) : EmptyRule(config) {

    override fun visitForExpression(expression: KtForExpression) {
        super.visitForExpression(expression)
        expression.body?.addFindingIfBlockExprIsEmpty()
    }
}
