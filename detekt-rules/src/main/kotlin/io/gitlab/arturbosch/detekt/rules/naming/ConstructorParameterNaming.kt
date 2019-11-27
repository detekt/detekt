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
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports constructor parameter names which do not follow the specified naming convention are used.
 *
 * @configuration parameterPattern - naming pattern (default: `'[a-z][A-Za-z0-9]*'`)
 * @configuration privateParameterPattern - naming pattern (default: `'[a-z][A-Za-z0-9]*'`)
 * @configuration excludeClassPattern - ignores variables in classes which match this regex (default: `'$^'`)
 * @configuration ignoreOverridden - ignores constructor properties that have the override modifier (default: `true`)
 *
 * @active since v1.0.0
 */
class ConstructorParameterNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Constructor parameter names should follow the naming convention set in the projects configuration.",
            debt = Debt.FIVE_MINS)

    private val parameterPattern by LazyRegex(PARAMETER_PATTERN, "[a-z][A-Za-z\\d]*")
    private val privateParameterPattern by LazyRegex(PRIVATE_PARAMETER_PATTERN, "[a-z][A-Za-z\\d]*")
    private val excludeClassPattern by LazyRegex(EXCLUDE_CLASS_PATTERN, "$^")
    private val ignoreOverridden = valueOrDefault(IGNORE_OVERRIDDEN, true)

    override fun visitParameter(parameter: KtParameter) {
        if (parameter.isContainingExcludedClassOrObject(excludeClassPattern) || isIgnoreOverridden(parameter)) {
            return
        }

        val identifier = parameter.identifierName()
        if (parameter.isPrivate()) {
            if (!identifier.matches(privateParameterPattern)) {
                report(CodeSmell(
                        issue,
                        Entity.from(parameter),
                        message = "Constructor private parameter names should " +
                                "match the pattern: $privateParameterPattern"))
            }
        } else {
            if (!identifier.matches(parameterPattern)) {
                report(CodeSmell(
                        issue,
                        Entity.from(parameter),
                        message = "Constructor parameter names should match the pattern: $parameterPattern"))
            }
        }
    }

    private fun isIgnoreOverridden(parameter: KtParameter) = ignoreOverridden && parameter.isOverride()

    companion object {
        const val PARAMETER_PATTERN = "parameterPattern"
        const val PRIVATE_PARAMETER_PATTERN = "privateParameterPattern"
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
        const val IGNORE_OVERRIDDEN = "ignoreOverridden"
    }
}
