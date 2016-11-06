package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class EmptyCatchBlock(config: Config) : EmptyRule("EmptyCatchBlock", config = config) {

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchBody?.addFindingIfBlockExprIsEmpty()
	}

}
