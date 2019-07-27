package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
 * @configuration imports - imports which should not be used (default: `''`)
 */
class ForbiddenImport(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Mark forbidden imports. A forbidden import could be an import for an unstable / experimental api" +
                "and hence you might want to mark it as forbidden in order to get warned about the usage.",
        Debt.TEN_MINS
    )

    private val forbiddenImports = valueOrDefault(IMPORTS, "").split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.replace(".", "\\.") }
        .map { it.replace("*", ".*") }
        .map { Regex(it) }

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)

        val import = importDirective.importedFqName?.asString() ?: ""
        if (forbiddenImports.any { it.matches(import) }) {
            report(
                CodeSmell(
                    issue, Entity.from(importDirective), "The import " +
                            "$import has been forbidden in the Detekt config."
                )
            )
        }
    }

    companion object {
        const val IMPORTS = "imports"
    }
}
