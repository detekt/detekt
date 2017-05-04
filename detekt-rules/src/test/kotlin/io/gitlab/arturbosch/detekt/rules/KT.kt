package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Unstable
import io.gitlab.arturbosch.detekt.core.KtCompiler
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
enum class Case(val file: String) {
	CasesFolder("/cases"),
	Default("/cases/Default.kt"),
	Empty("/cases/Empty.kt"),
	Exceptions("/cases/Exceptions.kt"),
	NamingConventions("/cases/NamingConventions.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	Comments("/cases/Comments.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NestedLongMethods("/cases/NestedLongMethods.kt"),
	FeatureEnvy("/cases/FeatureEnvy.kt"),
	SuppressedElements("/SuppressedByElementAnnotation.kt"),
	SuppressedElementsByFile("/SuppressedElementsByFileAnnotation.kt"),
	SuppressedElementsByClass("/SuppressedElementsByClassAnnotation.kt");

	fun path(): Path {
		val resource = Case::class.java.getResource(file)
		requireNotNull(resource)
		return Paths.get(resource.path)
	}
}

@Unstable
private val compiler = KtCompiler(Case.CasesFolder.path())

@Unstable
fun load(case: Case): KtFile {
	return compiler.compile(case.path())
}
