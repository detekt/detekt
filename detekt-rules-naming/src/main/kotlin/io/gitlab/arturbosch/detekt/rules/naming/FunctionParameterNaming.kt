package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.api.internal.configWithFallback
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClass
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Reports function parameter names which do not follow the specified naming convention are used.
 */
@ActiveByDefault(since = "1.0.0")
class FunctionParameterNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Function parameter names should follow the naming convention set in the projects configuration.",
        debt = Debt.FIVE_MINS
    )

    @Configuration("naming pattern")
    private val parameterPattern: Regex by config("[a-z][A-Za-z0-9]*", String::toRegex)

    @Configuration("ignores variables in classes which match this regex")
    private val excludeClassPattern: Regex by config("$^", String::toRegex)

    @Suppress("unused")
    @Configuration("ignores overridden functions with parameters not matching the pattern")
    @Deprecated("Use `ignoreOverridden` instead")
    private val ignoreOverriddenFunctions: Boolean by config(true)

    @Configuration("ignores overridden functions with parameters not matching the pattern")
    private val ignoreOverridden: Boolean by configWithFallback("ignoreOverriddenFunctions", true)

    override fun visitParameter(parameter: KtParameter) {
        if (parameter.isContainingExcludedClass(excludeClassPattern)) {
            return
        }

        if (ignoreOverridden && parameter.ownerFunction?.isOverride() == true) {
            return
        }

        val identifier = parameter.identifierName()
        if (!identifier.matches(parameterPattern)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(parameter),
                    message = "Function parameter names should match the pattern: $parameterPattern"
                )
            )
        }
    }
}
