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
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClassOrObject
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports constructor parameter names which do not follow the specified naming convention are used.
 */
@ActiveByDefault(since = "1.0.0")
class ConstructorParameterNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Constructor parameter names should follow the naming convention set in the projects configuration.",
        debt = Debt.FIVE_MINS
    )

    @Configuration("naming pattern")
    private val parameterPattern: Regex by config("[a-z][A-Za-z0-9]*") { it.toRegex() }

    @Configuration("naming pattern")
    private val privateParameterPattern: Regex by config("[a-z][A-Za-z0-9]*") { it.toRegex() }

    @Configuration("ignores variables in classes which match this regex")
    private val excludeClassPattern: Regex by config("$^") { it.toRegex() }

    @Configuration("ignores constructor properties that have the override modifier")
    private val ignoreOverridden: Boolean by config(true)

    override fun visitParameter(parameter: KtParameter) {
        if (parameter.isContainingExcludedClassOrObject(excludeClassPattern) || isIgnoreOverridden(parameter)) {
            return
        }

        val identifier = parameter.identifierName()
        if (parameter.isPrivate()) {
            if (!identifier.matches(privateParameterPattern)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(parameter),
                        message = "Constructor private parameter names should " +
                            "match the pattern: $privateParameterPattern"
                    )
                )
            }
        } else {
            if (!identifier.matches(parameterPattern)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(parameter),
                        message = "Constructor parameter names should match the pattern: $parameterPattern"
                    )
                )
            }
        }
    }

    private fun isIgnoreOverridden(parameter: KtParameter) = ignoreOverridden && parameter.isOverride()
}
