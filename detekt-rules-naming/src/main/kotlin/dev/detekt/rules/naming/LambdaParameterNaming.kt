package dev.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Reports lambda parameter names that do not follow the specified naming convention.
 */
class LambdaParameterNaming(config: Config) :
    Rule(config, "Lambda parameter names should follow the naming convention set in detekt's configuration.") {

    @Configuration("naming pattern")
    private val parameterPattern: Regex by config("[a-z][A-Za-z0-9]*|_", String::toRegex)

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        lambdaExpression.valueParameters
            .flatMap { it.getNamedDeclarations() }
            .mapNotNull { it.nameIdentifier }
            .forEach {
                val identifier = it.text
                if (!identifier.matches(parameterPattern)) {
                    report(
                        Finding(
                            Entity.from(it),
                            message = "Lambda parameter names should match the pattern: $parameterPattern",
                        )
                    )
                }
            }
    }

    private fun KtParameter.getNamedDeclarations(): List<KtNamedDeclaration> =
        this.destructuringDeclaration?.entries ?: listOf(this)
}
