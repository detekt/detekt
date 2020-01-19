package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
class NestedClassesVisibility(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("NestedClassesVisibility", Severity.Style,
            "The explicit public modifier still results in an internal nested class.",
            Debt.FIVE_MINS)

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface() && klass.isTopLevel() && klass.isInternal()) {
            checkDeclarations(klass)
        }
    }

    private fun checkDeclarations(klass: KtClass) {
        klass.declarations
                .filterIsInstance<KtClassOrObject>()
                .filter { it.hasModifier(KtTokens.PUBLIC_KEYWORD) && it.isNoEnum() && it.isNoCompanionObj() }
                .forEach { report(CodeSmell(issue, Entity.from(it), issue.description)) }
    }

    private fun KtClassOrObject.isNoEnum() = !this.hasModifier(KtTokens.ENUM_KEYWORD) && this !is KtEnumEntry

    private fun KtClassOrObject.isNoCompanionObj() = !this.hasModifier(KtTokens.COMPANION_KEYWORD)
}
