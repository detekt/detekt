package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports constructor parameter names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class ConstructorParameterNaming(config: Config) : Rule(
    config,
    "Constructor parameter names should follow the naming convention set in detekt's configuration."
) {

    @Configuration("naming pattern")
    private val parameterPattern: Regex by config("[a-z][A-Za-z0-9]*") { it.toRegex() }

    @Configuration("naming pattern")
    private val privateParameterPattern: Regex by config("[a-z][A-Za-z0-9]*") { it.toRegex() }

    @Configuration("ignores variables in classes which match this regex")
    private val excludeClassPattern: Regex by config("$^") { it.toRegex() }

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
                    Finding(
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
                Finding(
                    Entity.from(parameter),
                    message = "Constructor private parameter names should match the pattern: $privateParameterPattern"
                )
            )
        }
    }

    private fun KtParameter.isConstructor(): Boolean = this.ownerFunction is KtConstructor<*>
}
