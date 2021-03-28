package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports empty Kotlin (.kt) files. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyKtFile(config: Config) : EmptyRule(config) {

    override fun visitKtFile(file: KtFile) {
        if (file.text.isNullOrBlank()) {
            report(
                CodeSmell(
                    issue,
                    Entity.atPackageOrFirstDecl(file),
                    "The empty Kotlin file ${file.name} can be removed."
                )
            )
        }
    }
}
