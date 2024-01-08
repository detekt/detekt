package io.gitlab.arturbosch.detekt.sample.extensions.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule is a copy of [TooManyFunctions] which allows the threshold to be configured
 * in the detekt configuration file.
 */
class TooManyFunctionsTwo(config: Config) : Rule(config, "Too many functions can make the maintainability of a file more costly.") {

    private val allowedFunctions: Int by config(defaultValue = 10)

    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > allowedFunctions) {
            report(
                CodeSmell(
                    issue,
                    entity = Entity.from(file),
                    message = "The file ${file.name} has $amount function declarations. " +
                        "The maximum number of allowed functions is specified with $allowedFunctions.",
                    references = emptyList()
                )
            )
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }
}
