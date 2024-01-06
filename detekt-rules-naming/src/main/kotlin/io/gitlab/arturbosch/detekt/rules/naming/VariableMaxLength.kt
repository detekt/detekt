package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Reports when very long variable names are used.
 */
class VariableMaxLength(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "Variable names should not be longer than the maximum set in the configuration.",
    )

    @Configuration("maximum name length")
    private val maximumVariableNameLength: Int by config(DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH)

    override fun visitProperty(property: KtProperty) {
        if (property.isOverride()) {
            return
        }

        if (property.identifierName().length > maximumVariableNameLength) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(property),
                    message = "Variable names should be at most $maximumVariableNameLength characters long."
                )
            )
        }
    }

    companion object {
        private const val DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH = 64
    }
}
