package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration

/**
 * Destructuring declarations with too many entries are hard to read and understand.
 * To increase readability they should be refactored to reduce the number of entries or avoid using a destructuring
 * declaration.
 *
 * <noncompliant>
 * data class TooManyElements(val a: Int, val b: Int, val c: Int, val d: Int)
 * val (a, b, c, d) = TooManyElements(1, 2, 3, 4)
 * </noncompliant>
 * <compliant>
 * data class FewerElements(val a: Int, val b: Int, val c: Int)
 * val (a, b, c) = TooManyElements(1, 2, 3)
 * </compliant>
 *
 * @configuration maxDestructuringEntries - maximum allowed elements in a destructuring declaration (default: `3`)
 */
class DestructuringDeclarationWithTooManyEntries(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "The destructuring declaration contains too many entries, making it difficult to read. Consider refactoring " +
                "to avoid using a destructuring declaration for this case.",
        Debt.TEN_MINS
    )

    private val maxDestructuringEntries = valueOrDefault(MAX_DESTRUCTURING_ENTRIES, 3)

    override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
        if (destructuringDeclaration.entries.size > maxDestructuringEntries) {
            report(CodeSmell(issue, Entity.from(destructuringDeclaration), issue.description))
        }
        super.visitDestructuringDeclaration(destructuringDeclaration)
    }

    companion object {
        const val MAX_DESTRUCTURING_ENTRIES = "maxDestructuringEntries"
    }
}
