package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
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
 *
 * <compliant>
 * data class FewerElements(val a: Int, val b: Int, val c: Int)
 * val (a, b, c) = TooManyElements(1, 2, 3)
 * </compliant>
 */
class DestructuringDeclarationWithTooManyEntries(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Too many entries in a destructuring declaration make the code hard to understand.",
        Debt.TEN_MINS
    )

    @Configuration("maximum allowed elements in a destructuring declaration")
    private val maxDestructuringEntries: Int by config(3)

    override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
        if (destructuringDeclaration.entries.size > maxDestructuringEntries) {
            val message = "The destructuring declaration contains ${destructuringDeclaration.entries.size} but only " +
                "$maxDestructuringEntries are allowed."
            report(CodeSmell(issue, Entity.from(destructuringDeclaration), message))
        }
        super.visitDestructuringDeclaration(destructuringDeclaration)
    }
}
