package io.gitlab.arturbosch.detekt.rules.documentation

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.documentation.internal.isPublicInherited
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule will report any public property which does not have the required documentation.
 * This also includes public properties defined in a primary constructor.
 * If the codebase should have documentation on all public properties enable this rule to enforce this.
 * Overridden properties are excluded by this rule.
 */
class UndocumentedPublicProperty(config: Config) : Rule(
    config,
    "Public properties require documentation."
) {

    @Configuration("if protected functions should be searched")
    private val searchProtectedProperty: Boolean by config(false)

    @Configuration("ignores a enum entries when set to true")
    private val ignoreEnumEntries: Boolean by config(false)

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (constructor.isPublicInherited()) {
            val comment = constructor.containingClassOrObject?.docComment?.text
            constructor.valueParameters
                .filter { it.isPublicNotOverridden() && it.hasValOrVar() }
                .filter { it.isUndocumented(comment) && it.docComment == null }
                .forEach { report(it) }
        }
        super.visitPrimaryConstructor(constructor)
    }

    override fun visitProperty(property: KtProperty) {
        if (property.isPublicInherited(searchProtectedProperty) && !property.isLocal && property.shouldBeDocumented()) {
            report(property)
        }
        super.visitProperty(property)
    }

    override fun visitEnumEntry(enumEntry: KtEnumEntry) {
        super.visitEnumEntry(enumEntry)
        if (
            !ignoreEnumEntries &&
            enumEntry.isPublicInherited(searchProtectedProperty) &&
            enumEntry.docComment == null
        ) {
            report(enumEntry)
        }
    }

    private fun KtNamedDeclaration.isUndocumented(comment: String?) =
        comment == null || isNotReferenced(comment)

    private fun KtNamedDeclaration.isNotReferenced(comment: String): Boolean {
        val name = nameAsSafeName
        return !comment.contains("[$name]") && !comment.contains("@property $name") && !comment.contains("@param $name")
    }

    private fun KtProperty.shouldBeDocumented() =
        docComment == null &&
            isTopLevelOrInPublicClass() &&
            isPublicNotOverridden(searchProtectedProperty) &&
            this.isUndocumented(this.containingClassOrObject?.docComment?.text)

    private fun KtProperty.isTopLevelOrInPublicClass() = isTopLevel || containingClassOrObject?.isPublic == true

    private fun report(property: KtNamedDeclaration) {
        report(
            Finding(
                Entity.atName(property),
                "The property ${property.nameAsSafeName} is missing documentation."
            )
        )
    }
}
