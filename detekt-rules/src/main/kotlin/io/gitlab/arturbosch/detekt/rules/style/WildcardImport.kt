package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.psi.KtImportDirective

/**
 * Wildcard imports should be replaced with imports using fully qualified class names. This helps increase clarity of
 * which classes are imported and helps prevent naming conflicts.
 *
 * Library updates can introduce naming clashes with your own classes which might result in compilation errors.
 *
 * **NOTE:** This rule is effectively overridden by the `NoWildcardImports` formatting rule (a wrapped ktlint rule).
 * That rule will fail the check regardless of the whitelist configured here.
 * Therefore if whitelist is needed `NoWildcardImports` rule should be disabled.
 *
 * <noncompliant>
 * import io.gitlab.arturbosch.detekt.*
 *
 * class DetektElements {
 *     val element1 = DetektElement1()
 *     val element2 = DetektElement2()
 * }
 * </noncompliant>
 *
 * <compliant>
 * import io.gitlab.arturbosch.detekt.DetektElement1
 * import io.gitlab.arturbosch.detekt.DetektElement2
 *
 * class DetektElements {
 *     val element1 = DetektElement1()
 *     val element2 = DetektElement2()
 * }
 * </compliant>
 *
 * @configuration excludeImports - Define a whitelist of package names that should be allowed to be imported
 * with wildcard imports. (default: `['java.util.*', 'kotlinx.android.synthetic.*']`)
 *
 * @active since v1.0.0
 */
class WildcardImport(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Wildcard imports should be replaced with imports using fully qualified class names. " +
                    "Wildcard imports can lead to naming conflicts. " +
                    "A library update can introduce naming clashes with your classes which " +
                    "results in compilation errors.",
            Debt.FIVE_MINS)

    private val excludedImports = valueOrDefaultCommaSeparated(
            EXCLUDED_IMPORTS, listOf("java.util.*", "kotlinx.android.synthetic.*"))
        .map { it.removePrefix("*").removeSuffix("*") }

    override fun visitImportDirective(importDirective: KtImportDirective) {
        val import = importDirective.importPath?.pathStr
        if (import != null) {
            if (!import.contains("*")) {
                return
            }

            if (excludedImports.any { import.contains(it, ignoreCase = true) }) {
                return
            }
            report(CodeSmell(issue, Entity.from(importDirective), "$import " +
                    "is a wildcard import. Replace it with fully qualified imports."))
        }
    }

    companion object {
        const val EXCLUDED_IMPORTS = "excludeImports"
    }
}
