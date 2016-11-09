package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchRuntimeException(config: Config = Config.empty) : ExceptionsRule("CatchRuntimeException", config) {

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact { "RuntimeException" }
	}

}