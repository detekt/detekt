package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.simplePatternToRegex
import dev.detekt.api.valuesWithReason
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * Reports all imports that are forbidden.
 *
 * This rule allows to set a list of forbidden [forbiddenImports].
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

    @Configuration(
        "List of imports, specified as glob patterns, that are forbidden. " +
            "It is recommended to also specify a reason."
    )
    private val forbiddenImports: List<Forbidden> by config(valuesWithReason()) { list ->
        list.map { Forbidden(it.value.simplePatternToRegex(), it.reason) }
    }

    @Configuration(
        "List of imports, specified as glob patterns, to explicitly allow. " +
            "Use this to specify exceptions to the forbidden imports."
    )
    private val allowedImports: List<Regex> by config(emptyList<String>()) { list ->
        list.map { it.simplePatternToRegex() }
    }

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)

        val import = importDirective.importedFqName?.asString() ?: return
        val forbidden = forbiddenImports.find { it.import.matches(import) } ?: return

        if (importIsExplicitlyAllowed(import)) {
            return
        }
        val reason = forbidden.reason?.let { "The import `$import` has been forbidden: ${forbidden.reason}" }
            ?: defaultReason(import)

        report(Finding(Entity.from(importDirective), reason))
    }

    private fun defaultReason(forbiddenImport: String): String =
        "The import `$forbiddenImport` has been forbidden in the detekt config."

    private fun importIsExplicitlyAllowed(import: String): Boolean =
        allowedImports.any { it.matches(import) }
}

private data class Forbidden(val import: Regex, val reason: String?)
