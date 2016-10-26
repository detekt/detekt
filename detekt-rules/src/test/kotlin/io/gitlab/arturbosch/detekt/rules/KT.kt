package io.gitlab.arturbosch.detekt.rules

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
	NamingConventions("/cases/NamingConventions.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	Comments("/cases/Comments.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NestedLongMethods("/cases/NestedLongMethods.kt");

	fun path(): Path = Paths.get(Case::class.java.getResource(file).path)
}

fun load(case: Case): KtFile {
	return compiler.compile(case.path())
}
