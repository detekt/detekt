package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

/**
 * Reports empty secondary constructors. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptySecondaryConstructor(config: Config) : EmptyRule(config) {

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        constructor.bodyExpression?.addFindingIfBlockExprIsEmpty()
    }
}
