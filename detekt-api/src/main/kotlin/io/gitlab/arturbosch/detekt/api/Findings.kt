package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * @author Artur Bosch
 */

interface Finding {
	val id: String
	fun compact(): String
}

data class CodeSmell(override val id: String, val location: Location) : Finding {
	override fun compact(): String {
		return "$id - ${location.text} - ${location.file}"
	}
}

data class Location(val source: SourceLocation,
					val text: TextLocation,
					val locationString: String,
					val file: String) {

	companion object {
		fun of(element: KtElement): Location {
			val start = startLineAndColumn(element)
			val sourceLocation = SourceLocation(start.line, start.column)
			val textLocation = TextLocation(element.startOffset, element.endOffset)
			return Location(sourceLocation, textLocation,
					element.getTextWithLocation(), element.getContainingKtFile().name)
		}

		private fun startLineAndColumn(element: KtElement) = DiagnosticUtils.getLineAndColumnInPsiFile(
				element.getContainingKtFile(), element.textRange)
	}

}

data class SourceLocation(val line: Int, val column: Int) {
	override fun toString(): String {
		return "($line,$column)"
	}
}

data class TextLocation(val start: Int, val end: Int) {
	override fun toString(): String {
		return "($start,$end)"
	}
}