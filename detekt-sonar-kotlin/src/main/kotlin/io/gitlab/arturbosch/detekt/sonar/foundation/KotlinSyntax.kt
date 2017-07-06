package io.gitlab.arturbosch.detekt.sonar.foundation

import io.gitlab.arturbosch.detekt.api.FACTORY
import io.gitlab.arturbosch.detekt.api.isPartOf
import io.gitlab.arturbosch.detekt.api.visitTokens
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.highlighting.NewHighlighting
import org.sonar.api.batch.sensor.highlighting.TypeOfText

/**
 * @author Artur Bosch
 */
object KotlinSyntax {

	fun processFile(inputFile: InputFile, context: SensorContext) {
		val file = inputFile.file()

		val syntax = context.newHighlighting().onFile(inputFile)

		val ktFile = FACTORY.createFile(file.readText())
		ktFile.node.visitTokens {
			when (it.elementType) {
				in KtTokens.KEYWORDS -> syntax.highlightByType(it, TypeOfText.KEYWORD)
				in KtTokens.SOFT_KEYWORDS -> syntax.highlightByType(it, TypeOfText.KEYWORD_LIGHT)
				in KtTokens.STRINGS -> syntax.highlightByType(it, TypeOfText.STRING)
				in KtTokens.COMMENTS -> syntax.highlightByType(it, TypeOfText.COMMENT)
				KtTokens.SHORT_TEMPLATE_ENTRY_START -> syntax.highlightByType(it, TypeOfText.STRING)
				KtTokens.LONG_TEMPLATE_ENTRY_START -> syntax.highlightByType(it, TypeOfText.STRING)
				KtTokens.LONG_TEMPLATE_ENTRY_END -> syntax.highlightByType(it, TypeOfText.STRING)
				KtTokens.AT -> syntax.handleAnnotations(it)
			}
		}

		syntax.save()
	}

	private fun NewHighlighting.highlightByType(astNode: ASTNode, type: TypeOfText) {
		val range = astNode.textRange
		highlight(range.startOffset, range.endOffset, type)
	}
	
	private fun NewHighlighting.handleAnnotations(astNode: ASTNode) {
		val psi = astNode.psi
		if (psi.isPartOf(KtAnnotationEntry::class)) {
			val annotation = psi.getNonStrictParentOfType(KtAnnotationEntry::class.java)
			if (annotation != null) {
				val range = annotation.textRange
				highlight(range.startOffset, range.endOffset, TypeOfText.ANNOTATION)
			}
		}
	}
}


