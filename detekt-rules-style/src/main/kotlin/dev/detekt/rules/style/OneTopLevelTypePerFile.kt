package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule enforces that each Kotlin source file contains at most one top-level type declaration.
 * Top-level functions and properties do not count toward this limit. Nested classes, interfaces, objects,
 * companion objects, and local classes are ignored.
 *
 * <noncompliant>
 * class User
 *
 * interface UserRepository
 * </noncompliant>
 *
 * <compliant>
 * class User {
 *     class Address
 *
 *     companion object
 * }
 * </compliant>
 */
class OneTopLevelTypePerFile(config: Config) :
    Rule(config, "Each Kotlin source file should declare at most one top-level type.") {

    override fun visitKtFile(file: KtFile) {
        val topLevelTypes = file.declarations.filterIsInstance<KtClassOrObject>()

        if (topLevelTypes.size > 1) {
            report(
                Finding(
                    Entity.atName(topLevelTypes[1]),
                    "The file contains ${topLevelTypes.size} top-level type declarations. " +
                        "Each file may contain only one top-level type declaration."
                )
            )
        }
    }
}
