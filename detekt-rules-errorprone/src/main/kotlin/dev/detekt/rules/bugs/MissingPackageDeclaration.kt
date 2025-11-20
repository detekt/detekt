package dev.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports when the package declaration is missing.
 */
@ActiveByDefault(since = "2.0.0")
class MissingPackageDeclaration(config: Config) : Rule(
    config,
    "Kotlin source files should define a package."
) {

    override fun visitKtFile(file: KtFile) {
        if (file.packageDirective?.text.isNullOrBlank()) {
            report(Finding(Entity.from(file), "The file does not contain a package declaration."))
        }
    }
}
