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
 * This rule checks for list of default imports.
 * See [kotlin documentation](https://kotlinlang.org/docs/packages.html#imports).
 */
class RedundantExplicitImport(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Mark default import. Default imported don't need to be specified explicitly.",
        Debt.FIVE_MINS
    )

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)

        val import = importDirective.importedFqName?.asString() ?: ""
        if (defaultImports.contains(import.dropLastWhile { it != '.' })) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(importDirective),
                    "The import " +
                            "$import is default import and don't need to be imported explicitly."
                )
            )
        }
    }
}

// As of Kotlin 1.5
val defaultImports = setOf(
    "kotlin.",
    "kotlin.annotation.",
    "kotlin.collections.",
    "kotlin.comparisons.",
    "kotlin.io.",
    "kotlin.ranges.",
    "kotlin.sequences.",
    "kotlin.text.",
    // JVM specific
    "java.lang.",
    "kotlin.jvm.",
)
