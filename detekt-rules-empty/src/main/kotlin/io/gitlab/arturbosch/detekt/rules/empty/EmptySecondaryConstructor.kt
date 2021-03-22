package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

/**
 * Reports empty secondary constructors. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault("v1.0.0")
class EmptySecondaryConstructor(config: Config) : EmptyRule(config) {

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        constructor.bodyExpression?.addFindingIfBlockExprIsEmpty()
    }
}
