package io.gitlab.arturbosch.detekt.api

import io.github.detekt.psi.FilePath
import io.github.detekt.psi.getLineAndColumnInPsiFile
import io.github.detekt.psi.toFilePath
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Paths

/**
 * Specifies a position within a source code fragment.
 */
data class Location
@Deprecated("Consider relative path by passing a [FilePath]")
@JvmOverloads
constructor(
    val source: SourceLocation,
    val text: TextLocation,
    @Deprecated(
        "Use filePath instead",
        ReplaceWith(
            "filePath.absolutePath.toString()"
        )
    )
    val file: String,
    val filePath: FilePath = FilePath.fromAbsolute(Paths.get(file))
) : Compactable {
    var endSource: SourceLocation = source
        private set

    @Suppress("DEPRECATION")
    constructor(
        source: SourceLocation,
        text: TextLocation,
        filePath: FilePath
    ) : this(source, text, filePath.absolutePath.toString(), filePath)

    @Suppress("DEPRECATION")
    constructor(
        source: SourceLocation,
        endSource: SourceLocation,
        text: TextLocation,
        filePath: FilePath
    ) : this(source, text, filePath.absolutePath.toString(), filePath) {
        this.endSource = endSource
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        "locationString was removed and won't get passed to the main constructor. Use queries on 'ktElement' instead.",
        ReplaceWith(
            "Location(source, text, file)",
            "io.gitlab.arturbosch.detekt.api.Location"
        )
    )
    constructor(
        source: SourceLocation,
        text: TextLocation,
        @Suppress("UNUSED_PARAMETER") locationString: String,
        file: String
    ) : this(source, text, file)

    override fun compact(): String = "${filePath.absolutePath}:$source"

    companion object {
        /**
         * Creates a [Location] from a [PsiElement].
         * If the element can't be determined, the [KtFile] with a character offset can be used.
         */
        fun from(element: PsiElement, offset: Int = 0): Location {
            val start = startLineAndColumn(element, offset)
            val sourceLocation = SourceLocation(start.line, start.column)
            val end = endLineAndColumn(element, offset)
            val endSourceLocation = SourceLocation(end.line, end.column)
            val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
            return Location(sourceLocation, endSourceLocation, textLocation, element.containingFile.toFilePath())
        }

        /**
         * Determines the start line and column of a [PsiElement] in the source file.
         */
        fun startLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn =
            lineAndColumn(
                element,
                TextRange(element.textRange.startOffset + offset, element.textRange.endOffset + offset)
            )

        /**
         * Determines the end line and column of a [PsiElement] in the source file.
         */
        private fun endLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn =
            lineAndColumn(
                element,
                TextRange(element.textRange.endOffset + offset, element.textRange.endOffset + offset)
            )

        private fun lineAndColumn(element: PsiElement, range: TextRange): PsiDiagnosticUtils.LineAndColumn {
            return getLineAndColumnInPsiFile(element.containingFile, range)
                ?: PsiDiagnosticUtils.LineAndColumn(-1, -1, null)
        }
    }
}

/**
 * Stores line and column information of a location.
 */
data class SourceLocation(val line: Int, val column: Int) {
    override fun toString(): String = "$line:$column"
}

/**
 * Stores character start and end positions of a text file.
 */
data class TextLocation(val start: Int, val end: Int) {
    override fun toString(): String = "$start:$end"
}
