package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtClassInitializer

/**
 * @active since v1.0.0
 * @author schalkms
 * @author Marvin Ramin
 */
class EmptyInitBlock(config: Config) : EmptyRule(config) {

	override fun visitClassInitializer(initializer: KtClassInitializer) {
		initializer.body?.addFindingIfBlockExprIsEmpty()
	}
}
