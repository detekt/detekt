package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * Specifies a position within a source code fragment.
 *
 * @author Artur Bosch
 */
data class Location(val source: SourceLocation,
					val text: TextLocation,
					val locationString: String,
					val file: String) : Compactable {

	override fun compact() = "$file:$source"

	companion object {
		fun from(element: PsiElement, offset: Int = 0): Location {
			val start = startLineAndColumn(element, offset)
			val sourceLocation = SourceLocation(start.line, start.column)
			val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
			val fileName = element.originalFilePath() ?: element.containingFile.name
			val locationText = element.getTextAtLocationSafe()
			return Location(sourceLocation, textLocation, locationText, fileName)
		}

		@Suppress("TooGenericExceptionCaught")
		fun startLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn {
			return try {
				val range = element.textRange
				DiagnosticUtils.getLineAndColumnInPsiFile(element.containingFile,
						TextRange(range.startOffset + offset, range.endOffset + offset))
			} catch (e: IndexOutOfBoundsException) {
				// #18 - somehow the TextRange is out of bound on '}' leaf nodes, returning fail safe -1
				PsiDiagnosticUtils.LineAndColumn(-1, -1, null)
			}
		}

		private fun PsiElement.originalFilePath()
				= (this.containingFile.viewProvider.virtualFile as LightVirtualFile).originalFile?.name

		private fun PsiElement.getTextAtLocationSafe()
				= getTextSafe({ searchName() }, { getTextWithLocation() })
	}

}

/**
 * Stores line and column information of a location.
 */
data class SourceLocation(val line: Int, val column: Int) {
	override fun toString() = "$line:$column"
}

/**
 * Stores character start and end positions of an text file.
 */
data class TextLocation(val start: Int, val end: Int) {
	override fun toString() = "$start:$end"
}
