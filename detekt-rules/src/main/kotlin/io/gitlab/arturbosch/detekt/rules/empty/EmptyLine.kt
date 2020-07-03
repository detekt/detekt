package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports empty line in Kotlin (.kt) files. Empty line of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyLine(config: Config) : EmptyRule(config) {

    private val twoAndMoreEmptyLine = Regex("\\n{3,}")

    override fun visitKtFile(file: KtFile) {
        if (file.text.contains(twoAndMoreEmptyLine)) {
            report(CodeSmell(
                issue,
                Entity.atPackageOrFirstDecl(file),
                "File ${file.name} contain empty lines that can be deleted."
            ))
        }
    }
}
