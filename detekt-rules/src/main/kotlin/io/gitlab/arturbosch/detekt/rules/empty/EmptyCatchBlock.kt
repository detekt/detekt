package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.rules.ALLOWED_EXCEPTION_NAME
import io.gitlab.arturbosch.detekt.rules.isAllowedExceptionName
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * Reports empty `catch` blocks. Empty blocks of code serve no purpose and should be removed.
 *
 * @configuration allowedExceptionNameRegex - ignores exception types which match this regex
 * (default: `"^(_|(ignore|expected).*)"`)
 * @active since v1.0.0
 */
class EmptyCatchBlock(config: Config) : EmptyRule(config = config) {

    private val allowedExceptionNameRegex by LazyRegex(ALLOWED_EXCEPTION_NAME_REGEX, ALLOWED_EXCEPTION_NAME)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        super.visitCatchSection(catchClause)
        if (catchClause.isAllowedExceptionName(allowedExceptionNameRegex)) {
            return
        }
        catchClause.catchBody?.addFindingIfBlockExprIsEmpty()
    }

    companion object {
        const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"
    }
}
