package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyElseBlock(config: Config) : EmptyIfElseBlock(config, KtIfExpression::getElse) {
    override val blockTypeStr = "else"
}
