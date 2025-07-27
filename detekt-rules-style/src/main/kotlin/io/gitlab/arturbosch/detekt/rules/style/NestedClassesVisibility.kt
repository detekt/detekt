package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isInternal
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * Nested classes inherit their visibility from the parent class
 * and are often used to implement functionality local to the class it is nested in.
 * These nested classes can't have a higher visibility than their parent.
 * However, the visibility can be further restricted by using a private modifier for instance.
 * In internal classes the _explicit_ public modifier for nested classes is misleading and thus unnecessary,
 * because the nested class still has an internal visibility.
 *
 * <noncompliant>
 * internal class Outer {
 *     // explicit public modifier still results in an internal nested class
 *     public class Nested
 * }
 * </noncompliant>
 *
 * <compliant>
 * internal class Outer {
 *     class Nested1
 *     internal class Nested2
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class NestedClassesVisibility(config: Config) : Rule(
    config,
    "The explicit public modifier still results in an internal nested class."
) {

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface() && klass.isTopLevel() && klass.isInternal()) {
            checkDeclarations(klass)
        }
    }

    private fun checkDeclarations(klass: KtClass) {
        klass.declarations
            .filterIsInstance<KtClassOrObject>()
            .filter { it.hasModifier(KtTokens.PUBLIC_KEYWORD) && it.isNoEnum() && it.isNoCompanionObj() }
            .forEach { report(Finding(Entity.from(it), description)) }
    }

    private fun KtClassOrObject.isNoEnum() = !this.hasModifier(KtTokens.ENUM_KEYWORD) && this !is KtEnumEntry

    private fun KtClassOrObject.isNoCompanionObj() = !this.hasModifier(KtTokens.COMPANION_KEYWORD)
}
