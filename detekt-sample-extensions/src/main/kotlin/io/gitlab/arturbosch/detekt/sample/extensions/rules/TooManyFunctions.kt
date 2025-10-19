package io.gitlab.arturbosch.detekt.sample.extensions.rules

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This is a sample rule reporting too many functions inside a file.
 */
class TooManyFunctions(
    config: Config,
) : Rule(config, "This rule reports a file with an excessive function count.") {

    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > THRESHOLD) {
            report(
                Finding(
                    Entity.atPackageOrFirstDecl(file),
                    message = "The file ${file.name} has $amount function declarations. " +
                        "Threshold is specified with $THRESHOLD."
                )
            )
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }

    companion object {
        private const val THRESHOLD = 10
    }
}
