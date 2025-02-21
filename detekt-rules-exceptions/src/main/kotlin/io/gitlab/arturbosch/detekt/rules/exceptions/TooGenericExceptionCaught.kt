package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isAllowedExceptionName
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * This rule reports `catch` blocks for exceptions that have a type that is too generic.
 * It should be preferred to catch specific exceptions to the case that is currently handled. If the scope of the caught
 * exception is too broad it can lead to unintended exceptions being caught.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ... do some I/O
 *     } catch(e: Exception) { } // too generic exception caught here
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ... do some I/O
 *     } catch(e: IOException) { }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class TooGenericExceptionCaught(config: Config) : Rule(
    config,
    "The caught exception is too generic. " +
        "Prefer catching specific exceptions to the case that is currently handled."
) {

    @Configuration("exceptions which are too generic and should not be caught")
    private val exceptionNames: Set<String> by config(caughtExceptionDefaults) { it.toSet() }

    @Configuration("ignores too generic exception types which match this regex")
    private val allowedExceptionNameRegex: Regex by config("_|(ignore|expected).*", String::toRegex)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        catchClause.catchParameter?.let { catchParameter ->
            if (isTooGenericException(catchParameter.typeReference) &&
                !catchClause.isAllowedExceptionName(allowedExceptionNameRegex)
            ) {
                report(Finding(Entity.from(catchParameter), description))
            }
        }
        super.visitCatchSection(catchClause)
    }

    private fun isTooGenericException(typeReference: KtTypeReference?): Boolean = typeReference?.text in exceptionNames

    companion object {
        val caughtExceptionDefaults = listOf(
            "ArrayIndexOutOfBoundsException",
            "Error",
            "Exception",
            "IllegalMonitorStateException",
            "IndexOutOfBoundsException",
            "NullPointerException",
            "RuntimeException",
            "Throwable",
        )
    }
}
