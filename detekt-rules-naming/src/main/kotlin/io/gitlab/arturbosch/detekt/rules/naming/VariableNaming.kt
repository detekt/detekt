package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
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
@Alias("PropertyName")
class VariableNaming(config: Config) : Rule(
    config,
    "Variable names should follow the naming convention set in the projects configuration."
) {

    @Configuration("naming pattern")
    private val variablePattern: Regex by config("[a-z][A-Za-z0-9]*", String::toRegex)

    @Configuration("naming pattern")
    private val privateVariablePattern: Regex by config("(_)?[a-z][A-Za-z0-9]*", String::toRegex)

    @Configuration("ignores variables in classes which match this regex")
    private val excludeClassPattern: Regex by config("$^", String::toRegex)

    override fun visitProperty(property: KtProperty) {
        if (property.isPropertyTopLevelOrInCompanion()) {
            return
        }
        if (property.isSingleUnderscore || property.isContainingExcludedClassOrObject(excludeClassPattern)) {
            return
        }

        if (property.isOverride()) {
            return
        }

        val identifier = property.name ?: return
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

    private fun KtProperty.isPropertyTopLevelOrInCompanion(): Boolean =
        this.nameAsSafeName.isSpecial ||
            this.getNonStrictParentOfType<KtObjectDeclaration>() != null ||
            this.isTopLevel ||
            this.nameIdentifier?.parent?.javaClass == null

    private fun report(property: KtProperty, message: String) {
        report(
            CodeSmell(
                Entity.atName(property),
                message = message
            )
        )
    }

    companion object {
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
    }
}
