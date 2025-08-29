package io.gitlab.arturbosch.detekt.sample.extensions.rules

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule is a copy of [TooManyFunctions] which allows the threshold to be configured
 * in the detekt configuration file.
 */
class TooManyFunctionsTwo(
    config: Config,
) : Rule(config, "Too many functions can make the maintainability of a file more costly.") {

    private val threshold: Int by config(defaultValue = 10)

    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > threshold) {
            report(
                Finding(
                    entity = Entity.from(file),
                    message = "The file ${file.name} has $amount function declarations. " +
                        "Threshold is specified with $threshold.",
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
