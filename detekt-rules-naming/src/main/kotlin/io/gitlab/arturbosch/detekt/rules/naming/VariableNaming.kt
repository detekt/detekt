package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

/**
 * Reports variable names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class VariableNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Variable names should follow the naming convention set in the projects configuration.",
        debt = Debt.FIVE_MINS
    )

    @Configuration("naming pattern")
    private val variablePattern: Regex by config("[a-z][A-Za-z0-9]*", String::toRegex)

    @Configuration("naming pattern")
    private val privateVariablePattern: Regex by config("(_)?[a-z][A-Za-z0-9]*", String::toRegex)

    @Configuration("ignores variables in classes which match this regex")
    private val excludeClassPattern: Regex by config("$^", String::toRegex)

    @Configuration("ignores member properties that have the override modifier")
    private val ignoreOverridden: Boolean by config(true)

    override fun visitProperty(property: KtProperty) {
        if (property.isPropertyTopLeveleOrInCompanion()) {
            return
        }
        if (property.isSingleUnderscore || property.isContainingExcludedClassOrObject(excludeClassPattern)) {
            return
        }

        if (ignoreOverridden && property.isOverride()) {
            return
        }

        val identifier = property.identifierName()
        if (property.isPrivate()) {
            if (!identifier.matches(privateVariablePattern)) {
                report(property, "Private variable names should match the pattern: $privateVariablePattern")
            }
        } else {
            if (!identifier.matches(variablePattern)) {
                report(property, "Variable names should match the pattern: $variablePattern")
            }
        }
    }

    private fun KtProperty.isPropertyTopLeveleOrInCompanion(): Boolean {
        return this.nameAsSafeName.isSpecial || this.getNonStrictParentOfType<KtObjectDeclaration>() == null || this.isTopLevel || this.nameIdentifier?.parent?.javaClass == null
    }

    private fun report(property: KtProperty, message: String) {
        report(
            CodeSmell(
                issue,
                Entity.atName(property),
                message = message
            )
        )
    }

    companion object {
        const val VARIABLE_PATTERN = "variablePattern"
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
    }
}
