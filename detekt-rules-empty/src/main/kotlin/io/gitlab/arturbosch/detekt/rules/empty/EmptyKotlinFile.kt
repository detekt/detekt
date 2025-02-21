package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports empty Kotlin (.kt, .kts) files. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyKotlinFile(config: Config) : EmptyRule(config) {

    override fun visitKtFile(file: KtFile) {
        var text = file.text
        val packageDirective = file.packageDirective
        if (packageDirective != null) {
            val range = packageDirective.textRange
            text = text.removeRange(range.startOffset, range.endOffset)
        }
        if (text.isNullOrBlank()) {
            report(
                Finding(
                    Entity.atPackageOrFirstDecl(file),
                    "The empty Kotlin file ${file.name} can be removed."
                )
            )
        }
    }
}
