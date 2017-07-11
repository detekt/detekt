package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchArrayIndexOutOfBoundsException(config: Config = Config.empty) : ExceptionsRule(config) {

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact { "ArrayIndexOutOfBoundsException" }
	}

}
