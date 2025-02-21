package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Reports empty `when` expressions. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyWhenBlock(config: Config) : EmptyRule(config) {

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)
        if (expression.entries.isEmpty()) {
            report(Finding(Entity.from(expression), "This when block is empty."))
        }
    }
}
