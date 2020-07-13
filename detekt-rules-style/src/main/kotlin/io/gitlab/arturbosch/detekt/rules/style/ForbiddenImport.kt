package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * This rule allows to set a list of forbidden imports. This can be used to discourage the use of unstable, experimental
 * or deprecated APIs. Detekt will then report all imports that are forbidden.
 *
 * <noncompliant>
 * package foo

 * import kotlin.jvm.JvmField
 * import kotlin.SinceKotlin
 * </noncompliant>
 *
 * @configuration imports - imports which should not be used (default: `[]`)
 * @configuration forbiddenPatterns - reports imports which match the specified regular expression. For example `net.*R`. (default: `''`)
 */
class ForbiddenImport(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Mark forbidden imports. A forbidden import could be an import for an unstable / experimental api" +
                "and hence you might want to mark it as forbidden in order to get warned about the usage.",
        Debt.TEN_MINS
    )

    private val forbiddenImports = valueOrDefaultCommaSeparated(IMPORTS, emptyList())
        .distinct()
        .map { it.simplePatternToRegex() }

    private val forbiddenPatterns: Regex = Regex(valueOrDefault(FORBIDDEN_PATTERNS, ""))

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)

        val import = importDirective.importedFqName?.asString() ?: ""
        if (forbiddenImports.any { it.matches(import) } || containsForbiddenPattern(import)) {
            report(
                CodeSmell(
                    issue, Entity.from(importDirective), "The import " +
                            "$import has been forbidden in the Detekt config."
                )
            )
        }
    }

    private fun containsForbiddenPattern(import: String): Boolean =
        forbiddenPatterns.pattern.isNotEmpty() && forbiddenPatterns.containsMatchIn(import)

    companion object {
        const val IMPORTS = "imports"
        const val FORBIDDEN_PATTERNS = "forbiddenPatterns"
    }
}
