package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicInherited
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule will report any public property which does not have the required documentation.
 * This also includes public properties defined in a primary constructor.
 * If the codebase should have documentation on all public properties enable this rule to enforce this.
 * Overridden properties are excluded by this rule.
 */
class UndocumentedPublicProperty(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Maintainability,
            "Public properties require documentation.", Debt.TWENTY_MINS)

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (constructor.isPublicInherited()) {
            val comment = constructor.containingClassOrObject?.docComment?.text
            constructor.valueParameters
                .filter { it.isPublicNotOverridden() && it.hasValOrVar() && it.isUndocumented(comment) }
                .forEach { report(it) }
        }
        super.visitPrimaryConstructor(constructor)
    }

    override fun visitProperty(property: KtProperty) {
        if (property.isPublicInherited() && !property.isLocal && property.shouldBeDocumented()) {
            report(property)
        }
        super.visitProperty(property)
    }

    private fun KtParameter.isUndocumented(comment: String?) =
        comment == null || isNotReferenced(comment)

    private fun KtParameter.isNotReferenced(comment: String): Boolean {
        val name = nameAsSafeName
        return !comment.contains("[$name]") && !comment.contains("@property $name") && !comment.contains("@param $name")
    }

    private fun KtProperty.shouldBeDocumented() =
        docComment == null && isTopLevelOrInPublicClass() && isPublicNotOverridden()

    private fun KtProperty.isTopLevelOrInPublicClass() = isTopLevel || containingClass()?.isPublic == true

    private fun report(property: KtNamedDeclaration) {
        report(
            CodeSmell(
                issue,
                Entity.atName(property),
                "The property ${property.nameAsSafeName} is missing documentation."
            )
        )
    }
}
