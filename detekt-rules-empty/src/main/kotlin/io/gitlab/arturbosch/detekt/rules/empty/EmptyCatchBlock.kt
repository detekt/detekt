package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.rules.ALLOWED_EXCEPTION_NAME
import io.gitlab.arturbosch.detekt.rules.isAllowedExceptionName
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * Reports empty `catch` blocks. Empty catch blocks indicate that an exception is ignored and not handled.
 * In case exceptions are ignored intentionally, this should be made explicit
 * by using the specified names in the `allowedExceptionNameRegex`.
 *
 * @configuration allowedExceptionNameRegex - ignores exception types which match this regex
 * (default: `'_|(ignore|expected).*'`)
 * @active since v1.0.0
 */
class EmptyCatchBlock(config: Config) : EmptyRule(
    config = config,
    description =
        "Empty catch block detected. " +
        "Empty catch blocks indicate that an exception is ignored and not handled.",
    codeSmellMessage =
        "Empty catch block detected. If the exception can be safely ignored, " +
        "name the exception according to one of the exemptions as per the configuration of this rule."
) {

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
