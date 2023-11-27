package io.gitlab.arturbosch.detekt.sample.extensions.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This is a sample rule reporting too many functions inside a file.
 */
class TooManyFunctions(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "This rule reports a file with an excessive function count.",
    )

    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > ALLOWED_FUNCTIONS) {
            report(
                CodeSmell(
                    issue,
                    Entity.atPackageOrFirstDecl(file),
                    message = "The file ${file.name} has $amount function declarations. " +
                        "The maximum number of allowed functions is specified with $ALLOWED_FUNCTIONS."
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
        private const val ALLOWED_FUNCTIONS = 10
    }
}
