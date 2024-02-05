package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import io.gitlab.arturbosch.detekt.api.valuesWithReason
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * Reports all imports that are forbidden.
 *
 * This rule allows to set a list of forbidden [imports].
 * This can be used to discourage the use of unstable, experimental or deprecated APIs.
 *
 * <noncompliant>
 * import kotlin.jvm.JvmField
 * import kotlin.SinceKotlin
 * </noncompliant>
 */
class ForbiddenImport(config: Config) : Rule(
    config,
    "Mark forbidden imports. A forbidden import could be an import for an unstable / experimental api " +
        "and hence you might want to mark it as forbidden in order to get warned about the usage."
) {

    @Configuration("imports which should not be used")
    private val imports: List<Forbidden> by config(valuesWithReason()) { list ->
        list.map { Forbidden(it.value.simplePatternToRegex(), it.reason) }
    }

    @Configuration("reports imports which match the specified regular expression. For example `net.*R`.")
    private val forbiddenPatterns: Regex by config("", String::toRegex)

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)

        val import = importDirective.importedFqName?.asString().orEmpty()

        val forbidden = imports.find { it.import.matches(import) }
        val reason = if (forbidden != null) {
            if (forbidden.reason != null) {
                "The import `$import` has been forbidden: ${forbidden.reason}"
            } else {
                defaultReason(import)
            }
        } else {
            if (containsForbiddenPattern(import)) defaultReason(import) else null
        }

        if (reason != null) {
            report(CodeSmell(Entity.from(importDirective), reason))
        }
    }

    private fun defaultReason(forbiddenImport: String): String {
        return "The import `$forbiddenImport` has been forbidden in the detekt config."
    }

    private fun containsForbiddenPattern(import: String): Boolean =
        forbiddenPatterns.pattern.isNotEmpty() && forbiddenPatterns.containsMatchIn(import)
}

private data class Forbidden(val import: Regex, val reason: String?)
