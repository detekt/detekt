package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `if` blocks. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyIfBlock(config: Config) : EmptyIfElseBlock(config, KtIfExpression::getThen) {
    override val blockTypeStr = "if"
}
