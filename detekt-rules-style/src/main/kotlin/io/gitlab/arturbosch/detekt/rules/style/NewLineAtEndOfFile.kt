package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.psi.getLineAndColumnInPsiFile
import io.github.detekt.psi.toFilePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset

/**
 * This rule reports files which do not end with a line separator.
 */
@ActiveByDefault(since = "1.0.0")
class NewLineAtEndOfFile(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "Checks whether files end with a line separator.",
    )

    override fun visitKtFile(file: KtFile) {
        val text = file.text
        if (text.isNotEmpty() && !text.endsWith('\n')) {
            val coords = getLineAndColumnInPsiFile(
                file,
                TextRange(file.endOffset, file.endOffset)
            )
            val sourceLocation = SourceLocation(coords?.line ?: 0, coords?.column ?: 0)
            val textLocation = TextLocation(file.endOffset, file.endOffset)
            val location = Location(
                source = sourceLocation,
                text = textLocation,
                filePath = file.containingFile.toFilePath()
            )
            report(
                CodeSmell(
                    issue,
                    Entity.from(file, location),
                    "The file ${file.name} is not ending with a new line."
                )
            )
        }
    }
}
