package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import dev.detekt.psi.pathGlobToRegex
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * Reports all imports that are forbidden.
 *
 * This rule allows to set a list of forbidden [forbiddenImports].
 * This can be used to discourage the use of unstable, experimental or deprecated APIs.
 * Imports are configured as glob patterns and may be exact imports such as `kotlin.jvm.JvmField`
 * or wildcard imports such as `java.util.*`. Each entry can also include a reason that is shown in the finding.
 * Use [allowedImports] to declare exceptions to broader forbidden globs:
 *
 * ```yaml
 * ForbiddenImport:
 *   active: true
 *   forbiddenImports:
 *     - value: 'kotlin.jvm.JvmField'
 *       reason: 'Use explicit backing properties instead.'
 *     - value: 'java.util.*'
 *       reason: 'Use Kotlin standard library APIs instead.'
 *   allowedImports:
 *     - 'java.util.UUID'
 * ```
 *
 * <noncompliant>
 * import kotlin.jvm.JvmField
 * import java.util.Date
 * </noncompliant>
 *
 * <compliant>
 * import java.util.UUID
 * </compliant>
 */
class ForbiddenImport(config: Config) :
    Rule(
        config,
        "Mark forbidden imports. A forbidden import could be an import for an unstable / experimental api " +
            "and hence you might want to mark it as forbidden in order to get warned about the usage."
    ) {

    @Configuration(
        "List of imports, specified as glob patterns, that are forbidden. " +
            "Entries may either be plain strings or value/reason pairs, allowing you to specify the reason why each " +
                "individual import is forbidden."
    )
    private val forbiddenImports: List<Forbidden> by config(valuesWithReason()) { list ->
        list.map { Forbidden(it.value.pathGlobToRegex(), it.reason) }
    }

    @Configuration(
        "List of imports, specified as glob patterns, to explicitly allow. " +
            "Use this to specify exceptions to broader forbidden imports."
    )
    private val allowedImports: List<Regex> by config(emptyList<String>()) { list ->
        list.map { it.pathGlobToRegex() }
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

    private fun importIsExplicitlyAllowed(import: String): Boolean = allowedImports.any { it.matches(import) }
}

private data class Forbidden(val import: Regex, val reason: String?)
