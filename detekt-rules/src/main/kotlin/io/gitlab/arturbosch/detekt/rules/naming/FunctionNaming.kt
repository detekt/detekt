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
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports when function names which do not follow the specified naming convention are used.
 * One exception are factory functions used to create instances of classes.
 * These factory functions can have the same name as the class being created.
 *
 * @configuration functionPattern - naming pattern (default: `'^([a-z$][a-zA-Z$0-9]*)|(`.*`)$'`)
 * @configuration excludeClassPattern - ignores functions in classes which match this regex (default: `'$^'`)
 * @configuration ignoreOverridden - ignores functions that have the override modifier (default: `true`)
 *
 * @active since v1.0.0
 */
class FunctionNaming(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("FunctionName")

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Function names should follow the naming convention set in the configuration.",
            debt = Debt.FIVE_MINS)

    private val functionPattern by LazyRegex(FUNCTION_PATTERN, "^([a-z$][a-zA-Z$0-9]*)|(`.*`)$")
    private val excludeClassPattern by LazyRegex(EXCLUDE_CLASS_PATTERN, "$^")
    private val ignoreOverridden = valueOrDefault(IGNORE_OVERRIDDEN, true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (ignoreOverridden && function.isOverride()) {
            return
        }

        if (!function.isContainingExcludedClassOrObject(excludeClassPattern) &&
                !function.identifierName().matches(functionPattern) &&
                function.identifierName() != function.typeReference?.name) {
            report(CodeSmell(
                    issue,
                    Entity.from(function),
                    message = "Function names should match the pattern: $functionPattern"))
        }
    }

    companion object {
        const val FUNCTION_PATTERN = "functionPattern"
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
        const val IGNORE_OVERRIDDEN = "ignoreOverridden"
    }
}
