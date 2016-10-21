package io.gitlab.arturbosch.detekt.rules

import com.intellij.lang.ASTNode
import io.gitlab.arturbosch.detekt.core.KtCompiler
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */

private val compiler = KtCompiler()

enum class Case(val file: String) {
	CasesFolder("/cases"),
	Default("/cases/Default.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NestedLongMethods("/cases/NestedLongMethods.kt");

	fun path(): Path = Paths.get(Case::class.java.getResource(file).path)
}

fun load(case: Case): ASTNode {
	return compiler.compile(case.path()).node
}

fun loadAsFile(content: String): ASTNode {
	return compiler.compileFromText(content).node
}

