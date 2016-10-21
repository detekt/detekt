package io.gitlab.arturbosch.detekt.api

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
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

class ThresholdedCodeSmell(id: String, location: Location, val value: Int, val threshold: Int) : CodeSmell(id, location) {
	override fun compact(): String {
		return "$id - $value/$threshold - l/c${location.source} - ${location.text} - ${location.file}"
	}
}

open class CodeSmell(override val id: String, val location: Location) : Finding {
	override fun compact(): String {
		return "$id - l/c${location.source} - ${location.text} - ${location.file}"
	}
}

data class Location(val source: SourceLocation,
					val text: TextLocation,
					val locationString: String,
					val file: String) {

	companion object {

		fun from(startElement: PsiElement, endElementExclusively: PsiElement?): Location {
			if (endElementExclusively == null) return of(startElement)
			val start = startLineAndColumn(startElement)
			val sourceLocation = SourceLocation(start.line, start.column)
			val textLocation = TextLocation(startElement.startOffset, endElementExclusively.startOffset - 1)
			return Location(sourceLocation, textLocation,
					startElement.getTextWithLocation(), startElement.containingFile.name)
		}

		fun of(element: PsiElement): Location {
			val start = startLineAndColumn(element)
			val sourceLocation = SourceLocation(start.line, start.column)
			val textLocation = TextLocation(element.startOffset, element.endOffset)
			return Location(sourceLocation, textLocation,
					element.getTextWithLocation(), element.containingFile.name)
		}

		private fun startLineAndColumn(element: PsiElement) = DiagnosticUtils.getLineAndColumnInPsiFile(
				element.containingFile, element.textRange)
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