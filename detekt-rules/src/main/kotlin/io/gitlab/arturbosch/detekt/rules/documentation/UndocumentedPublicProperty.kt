package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule will report any public property which does not have the required documentation.
 * If the codebase should have documentation on all public properties enable this rule to enforce this.
 * Overridden functions are excluded by this rule.
 */
class UndocumentedPublicProperty(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Maintainability,
            "Public properties require documentation.", Debt.TWENTY_MINS)

    override fun visitProperty(property: KtProperty) {
        if (!property.isLocal && property.docComment == null && property.shouldBeDocumented()) {
            report(CodeSmell(issue, Entity.from(property),
                    "The property ${property.nameAsSafeName} is missing documentation."))
        }
    }

    private fun KtProperty.shouldBeDocumented() =
        (isTopLevel || containingClass()?.isPublic == true) && isPublicNotOverridden()
}
