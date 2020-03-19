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
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClass
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Reports function parameter names which do not follow the specified naming convention are used.
 *
 * @configuration parameterPattern - naming pattern (default: `'[a-z][A-Za-z0-9]*'`)
 * @configuration excludeClassPattern - ignores variables in classes which match this regex (default: `'$^'`)
 * @configuration ignoreOverriddenFunctions - ignores overridden functions with parameters not matching the pattern
 * (default: `true`) (deprecated: "Use `ignoreOverridden` instead")
 * @configuration ignoreOverridden - ignores overridden functions with parameters not matching the pattern
 * (default: `true`)
 *
 * @active since v1.0.0
 */
class FunctionParameterNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Function parameter names should follow the naming convention set in the projects configuration.",
            debt = Debt.FIVE_MINS)

    private val parameterPattern by LazyRegex(PARAMETER_PATTERN, "[a-z][A-Za-z\\d]*")
    private val excludeClassPattern by LazyRegex(EXCLUDE_CLASS_PATTERN, "$^")
    private val ignoreOverridden = valueOrDefault(IGNORE_OVERRIDDEN, valueOrDefault(IGNORE_OVERRIDDEN_FUNCTIONS, true))

    override fun visitParameter(parameter: KtParameter) {
        if (parameter.isContainingExcludedClass(excludeClassPattern)) {
            return
        }

        if (ignoreOverridden && parameter.ownerFunction?.isOverride() == true) {
            return
        }

        val identifier = parameter.identifierName()
        if (!identifier.matches(parameterPattern)) {
            report(CodeSmell(
                    issue,
                    Entity.from(parameter),
                    message = "Function parameter names should match the pattern: $parameterPattern"))
        }
    }

    companion object {
        const val PARAMETER_PATTERN = "parameterPattern"
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
        const val IGNORE_OVERRIDDEN_FUNCTIONS = "ignoreOverriddenFunctions"
        const val IGNORE_OVERRIDDEN = "ignoreOverridden"
    }
}
