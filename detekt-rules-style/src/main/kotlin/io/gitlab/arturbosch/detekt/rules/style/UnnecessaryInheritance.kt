package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * This rule reports unnecessary super types. Inheriting from `Any` or `Object` is unnecessary and should simply be
 * removed.
 *
 * <noncompliant>
 * class A : Any()
 * class B : Object()
 * </noncompliant>
 */
@ActiveByDefault(since = "1.2.0")
class UnnecessaryInheritance(config: Config) : Rule(
    config,
    "The extended super type is unnecessary."
) {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        for (superEntry in classOrObject.superTypeListEntries) {
            when (superEntry.text) {
                "Any()" -> report(classOrObject, "Unnecessary inheritance of 'Any'.")
                "Object()" -> report(classOrObject, "Unnecessary inheritance of 'Object'.")
            }
        }
    }

    private fun report(classOrObject: KtClassOrObject, message: String) {
        report(Finding(Entity.atName(classOrObject), message))
    }
}
