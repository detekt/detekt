package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Reports function parameter names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class FunctionParameterNaming(config: Config) : Rule(
    config,
    "Function parameter names should follow the naming convention set in detekt's configuration."
) {

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

        val identifier = parameter.name ?: return
        if (!identifier.matches(parameterPattern)) {
            report(
                Finding(
                    Entity.from(parameter),
                    message = "Function parameter names should match the pattern: $parameterPattern"
                )
            )
        }
    }

    private fun KtParameter.isParameterInFunction(): Boolean =
        this.nameAsSafeName.isSpecial ||
            (this.nameIdentifier?.parent?.javaClass == null) ||
            (this.ownerFunction !is KtNamedFunction) ||
            this.isContainingExcludedClass(excludeClassPattern)
}
