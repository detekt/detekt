package dev.detekt.rules.style

import com.intellij.openapi.util.TextRange
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.psi.absolutePath
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset

/**
 * This rule reports files which do not end with a line separator.
 */
@ActiveByDefault(since = "1.0.0")
class NewLineAtEndOfFile(config: Config) : Rule(config, "Checks whether files end with a line separator.") {

    override fun visitKtFile(file: KtFile) {
        val text = file.text
        if (text.isNotEmpty() && !text.endsWith('\n')) {
            val coords = DiagnosticUtils.getLineAndColumnInPsiFile(
                file,
                TextRange(file.endOffset, file.endOffset)
            )
            val sourceLocation = SourceLocation(coords.line, coords.column)
            val textLocation = TextLocation(file.endOffset, file.endOffset)
            val location = Location(
                source = sourceLocation,
                endSource = sourceLocation,
                text = textLocation,
                path = file.containingFile.absolutePath()
            )
            report(
                Finding(
                    Entity.from(file, location),
                    "The file ${file.name} is not ending with a new line."
                )
            )
        }
    }
}
