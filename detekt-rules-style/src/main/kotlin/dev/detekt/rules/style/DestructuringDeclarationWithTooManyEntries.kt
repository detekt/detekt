package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
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
@ActiveByDefault(since = "1.21.0")
class DestructuringDeclarationWithTooManyEntries(config: Config) : Rule(
    config,
    "Too many entries in a destructuring declaration make the code hard to understand."
) {

    @Configuration("maximum allowed elements in a destructuring declaration")
    private val maxDestructuringEntries: Int by config(3)

    override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
        if (destructuringDeclaration.entries.size > maxDestructuringEntries) {
            val message = "The destructuring declaration contains ${destructuringDeclaration.entries.size} but only " +
                "$maxDestructuringEntries are allowed."
            report(Finding(Entity.from(destructuringDeclaration), message))
        }
        super.visitDestructuringDeclaration(destructuringDeclaration)
    }
}
