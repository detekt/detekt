package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.naming.identifierName
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * Reports empty `catch` blocks. Empty blocks of code serve no purpose and should be removed.
 *
 * @configuration allowedExceptionNameRegex - ignores exception types which match this regex
 * (default: "^(ignore|expected).*")
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 */
class EmptyCatchBlock(config: Config) : EmptyRule(config = config) {

	private val allowedExceptionNameRegex = Regex(valueOrDefault(ALLOWED_EXCEPTION_NAME_REGEX, "^(ignore|expected).*"))

	override fun visitCatchSection(catchClause: KtCatchClause) {
		val name = catchClause.catchParameter?.identifierName()
		if (name != null && name.matches(allowedExceptionNameRegex)) {
			return
		}
		catchClause.catchBody?.addFindingIfBlockExprIsEmpty()
	}

	companion object {
		const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"
	}

}
