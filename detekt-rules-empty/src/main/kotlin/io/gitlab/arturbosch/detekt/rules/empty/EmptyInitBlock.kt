package io.gitlab.arturbosch.detekt.rules.empty

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import org.jetbrains.kotlin.psi.KtClassInitializer

/**
 * Reports empty `init` expressions. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyInitBlock(config: Config) : EmptyRule(config) {

    override fun visitClassInitializer(initializer: KtClassInitializer) {
        initializer.body?.addFindingIfBlockExprIsEmpty()
    }
}
