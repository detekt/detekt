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
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Reports function parameter names that do not follow the specified naming convention.
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

    override fun visitParameter(parameter: KtParameter) {
        if (parameter.isParameterInFunction()) {
            return
        }

        if (parameter.ownerFunction?.isOverride() == true) {
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

    private fun KtParameter.isParameterInFunction(): Boolean {
        return this.nameAsSafeName.isSpecial ||
            (this.nameIdentifier?.parent?.javaClass == null) ||
            (this.ownerFunction !is KtNamedFunction) ||
            this.isContainingExcludedClass(excludeClassPattern)
    }
}
