package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClassOrObject
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

/**
 * Reports when variable names which do not follow the specified naming convention are used.
 *
 * @configuration variablePattern - naming pattern (default: `'[a-z][A-Za-z0-9]*'`)
 * @configuration privateVariablePattern - naming pattern (default: `'(_)?[a-z][A-Za-z0-9]*'`)
 * @configuration excludeClassPattern - ignores variables in classes which match this regex (default: `'$^'`)
 * @configuration ignoreOverridden - ignores member properties that have the override modifier (default: `true`)
 *
 * @active since v1.0.0
 */
class VariableNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Variable names should follow the naming convention set in the projects configuration.",
            debt = Debt.FIVE_MINS)

    private val variablePattern by LazyRegex(VARIABLE_PATTERN, "[a-z][A-Za-z0-9]*")
    private val privateVariablePattern by LazyRegex(PRIVATE_VARIABLE_PATTERN, "(_)?[a-z][A-Za-z0-9]*")
    private val excludeClassPattern by LazyRegex(EXCLUDE_CLASS_PATTERN, "$^")
    private val ignoreOverridden = valueOrDefault(IGNORE_OVERRIDDEN, true)

    override fun visitProperty(property: KtProperty) {
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

    private fun report(property: KtProperty, message: String) {
        report(CodeSmell(
            issue,
            Entity.atName(property),
            message = message))
    }

    companion object {
        const val VARIABLE_PATTERN = "variablePattern"
        const val PRIVATE_VARIABLE_PATTERN = "privateVariablePattern"
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
        const val IGNORE_OVERRIDDEN = "ignoreOverridden"
    }
}
