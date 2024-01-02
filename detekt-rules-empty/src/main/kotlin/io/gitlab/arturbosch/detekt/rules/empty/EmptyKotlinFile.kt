package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports empty Kotlin (.kt, .kts) files. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyKotlinFile(config: Config) : EmptyRule(config) {

    private val ignorePackageDeclaration = config.valueOrDefault(
        "ignorePackageDeclaration",
        false
    )

    override fun visitKtFile(file: KtFile) {
        var text = file.text
        if (ignorePackageDeclaration) {
            val packageDirective = file.packageDirective
            if (packageDirective != null) {
                val range = packageDirective.textRange
                text = text.removeRange(range.startOffset, range.endOffset)
            }
        }
        if (text.isNullOrBlank()) {
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
