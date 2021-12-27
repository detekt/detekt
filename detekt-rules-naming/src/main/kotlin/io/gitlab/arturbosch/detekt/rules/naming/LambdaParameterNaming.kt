package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Reports lambda parameter names that do not follow the specified naming convention.
 */
class LambdaParameterNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Lambda parameter names should follow the naming convention set in the projects configuration.",
        debt = Debt.FIVE_MINS
    )

    @Configuration("naming pattern")
    private val parameterPattern: Regex by config("[a-z][A-Za-z0-9]*|_", String::toRegex)

    override fun visitParameter(parameter: KtParameter) {
        val parameters: List<KtNamedDeclaration> = parameter.destructuringDeclaration?.entries ?: listOf(parameter)
        parameters
            .mapNotNull { it.nameIdentifier }
            .forEach {
                val identifier = it.text
                if (!identifier.matches(parameterPattern)) {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(it),
                            message = "Lambda parameter names should match the pattern: $parameterPattern",
                        )
                    )
                }
            }
    }
}
