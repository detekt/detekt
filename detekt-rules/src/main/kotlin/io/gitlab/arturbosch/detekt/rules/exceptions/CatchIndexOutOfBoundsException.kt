package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchIndexOutOfBoundsException(config: Config = Config.empty) : ExceptionsRule("CatchIndexOutOfBoundsException", config) {

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact { "IndexOutOfBoundsException" }
	}

}