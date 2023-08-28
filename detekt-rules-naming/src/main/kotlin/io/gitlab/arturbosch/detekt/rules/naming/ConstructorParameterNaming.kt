package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports constructor parameter names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class ConstructorParameterNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
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
    @Deprecated("This configuration is ignored and will be removed in the future")
    @Suppress("UnusedPrivateMember")
    private val ignoreOverridden: Boolean by config(true)

    override fun visitParameter(parameter: KtParameter) {
        if (!parameter.isConstructor() ||
            parameter.isContainingExcludedClassOrObject(excludeClassPattern) ||
            parameter.isOverride()
        ) {
            return
        }

        val identifier = parameter.name
        if (parameter.isPrivate()) {
            visitPrivateParameter(parameter)
        } else {
            if (identifier?.matches(parameterPattern) == false) {
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

    private fun visitPrivateParameter(parameter: KtParameter) {
        val identifier = parameter.name
        if (identifier?.matches(privateParameterPattern) == false) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(parameter),
                    message = "Constructor private parameter names should match the pattern: $privateParameterPattern"
                )
            )
        }
    }

    private fun KtParameter.isConstructor(): Boolean {
        return this.ownerFunction is KtConstructor<*>
    }
}
