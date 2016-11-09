package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchException(config: Config = Config.empty) : ExceptionsRule("CatchException", config) {

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact { "Exception" }
	}

}