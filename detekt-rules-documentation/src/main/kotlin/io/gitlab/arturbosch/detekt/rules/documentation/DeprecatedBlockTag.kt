package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * This rule reports use of the `@deprecated` block tag in KDoc comments. Deprecation must be specified using a
 * `@Deprecated` annotation as adding a `@deprecated` block tag in KDoc comments
 * [has no effect and is not supported](https://kotlinlang.org/docs/kotlin-doc.html#suppress). The `@Deprecated`
 * annotation constructor has dedicated fields for a message and a type (warning, error, etc.). You can also use the
 * `@ReplaceWith` annotation to specify how to solve the deprecation automatically via the IDE.
 *
 * <noncompliant>
 * /**
 *  * This function prints a message followed by a new line.
 *  *
 *  * @deprecated Useless, the Kotlin standard library can already do this. Replace with println.
 *  */
 * fun printThenNewline(what: String) {
 *     // ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * /**
 *  * This function prints a message followed by a new line.
 *  */
 * @@Deprecated("Useless, the Kotlin standard library can already do this.")
 * @@ReplaceWith("println(what)")
 * fun printThenNewline(what: String) {
 *     // ...
 * }
 * </compliant>
 */
class DeprecatedBlockTag(config: Config) : Rule(
    config,
    "Do not use the `@deprecated` block tag, which is not supported by KDoc. " +
        "Use the `@Deprecated` annotation instead."
) {

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        dcl.docComment?.getAllSections()?.forEach { section ->
            section.findTagsByName("deprecated").forEach { tag ->
                report(
                    Finding(
                        Entity.from(dcl),
                        "@deprecated tag block does not properly report deprecation in Kotlin, use @Deprecated " +
                            "annotation instead",
                        references = listOf(Entity.from(tag))
                    )
                )
            }
        }
    }
}
