package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.compileContentForTest
import io.gitlab.arturbosch.detekt.core.KtCompiler
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */

private val compiler = KtCompiler(Case.CasesFolder.path())

enum class Case(val file: String) {
	CasesFolder("/cases"),
	Default("/cases/Default.kt"),
	Empty("/cases/Empty.kt"),
	Exceptions("/cases/Exceptions.kt"),
	NamingConventions("/cases/NamingConventions.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	Comments("/cases/Comments.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NestedLongMethods("/cases/NestedLongMethods.kt");

	fun path(): Path {
		val resource = Case::class.java.getResource(file)
		requireNotNull(resource)
		return Paths.get(resource.path)
	}
}

fun Rule.lint(content: String): List<Finding> {
	val ktFile = compiler.compileFromText(content.trimIndent())
	this.visit(ktFile)
	return this.findings
}

fun Rule.format(content: String): String {
	val ktFile = compiler.compileFromText(content.trimIndent())
	this.visit(ktFile)
	return ktFile.text
}

fun load(case: Case): KtFile {
	return compiler.compile(case.path())
}
