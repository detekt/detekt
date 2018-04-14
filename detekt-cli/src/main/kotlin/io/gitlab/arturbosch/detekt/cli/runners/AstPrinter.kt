package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.cli.Args
import io.gitlab.arturbosch.detekt.core.KtCompiler
import io.gitlab.arturbosch.detekt.core.isFile
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.KtContainerNode
import org.jetbrains.kotlin.psi.KtDeclarationContainer
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStatementExpression

/**
 * @author Artur Bosch
 */
class AstPrinter(private val arguments: Args) : Executable {

	override fun execute() {
		assert(arguments.inputPath.size == 1) {
			"More than one input path specified. Printing AST is only supported for single files."
		}
		assert(arguments.inputPath[0].isFile()) {
			"Input path must be a kotlin file and not a directory."
		}

		val input = arguments.inputPath[0]
		val ktFile = KtCompiler().compile(input, input)
		ElementPrinter.print(ktFile)
	}
}

private class ElementPrinter : DetektVisitor() {

	companion object {
		private const val TAB = "\t"
		internal fun print(file: KtFile) = ElementPrinter().apply {
			println("0: " + file.javaClass.simpleName)
			visitKtFile(file)
		}
	}

	private val indentation
		get() = (0..indent).joinToString("") { "  " }

	private val KtElement.line
		get() = DiagnosticUtils.offsetToLineAndColumn(
				containingFile.viewProvider.document,
				textRange.startOffset).line

	private val KtElement.dump
		get() = indentation + line + ": " + javaClass.simpleName

	private var indent: Int = 0
	private var lastLine = 0

	override fun visitKtElement(element: KtElement) {
		val currentLine = element.line
		if (element.isContainer()) {
			indent++
			println(element.dump)
		} else {
			if (lastLine == currentLine) {
				indent++
				println(element.dump)
				indent--
			} else {
				println(element.dump)
			}
		}
		lastLine = currentLine
		super.visitKtElement(element)
		if (element.isContainer()) {
			indent--
		}
	}

	private fun KtElement.isContainer() =
			this is KtStatementExpression
					|| this is KtDeclarationContainer
					|| this is KtContainerNode
}
