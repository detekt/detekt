package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

fun Rule.lint(content: String): List<Finding> {
	val ktFile = KtTestCompiler.compileFromContent(content.trimIndent())
	return findingsAfterVisit(ktFile)
}

fun Rule.lint(path: Path): List<Finding> {
	val ktFile = KtTestCompiler.compile(path)
	return findingsAfterVisit(ktFile)
}

private fun Rule.findingsAfterVisit(ktFile: KtFile): List<Finding> {
	val context = Context()
	this.visit(context, ktFile)
	return context.findings
}

fun Rule.format(content: String): String {
	val ktFile = KtTestCompiler.compileFromContent(content.trimIndent())
	return contentAfterVisit(ktFile)
}

fun Rule.format(path: Path): String {
	val ktFile = KtTestCompiler.compile(path)
	return contentAfterVisit(ktFile)
}

private fun Rule.contentAfterVisit(ktFile: KtFile): String {
	val context = Context()
	this.visit(context, ktFile)
	return ktFile.text
}
