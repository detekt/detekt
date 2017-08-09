package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
enum class Case(val file: String) {
	CasesFolder("/cases"),
	Default("/cases/Default.kt"),
	Empty("/cases/Empty.kt"),
	EmptyDefaultConstructor("/cases/EmptyDefaultConstructor.kt"),
	Exceptions("/cases/Exceptions.kt"),
	NamingConventions("/cases/NamingConventions.kt"),
	NewLineAtEndOfFile("/cases/NewLineAtEndOfFile.kt"),
	MaxLineLength("/cases/MaxLineLength.kt"),
	ModifierOrder("/cases/ModifierOrder.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	Comments("/cases/Comments.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NestedLongMethods("/cases/NestedLongMethods.kt"),
	FeatureEnvy("/cases/FeatureEnvy.kt"),
	UnreachableCode("/cases/UnreachableCode.kt"),
	SuppressedElements("/SuppressedByElementAnnotation.kt"),
	SuppressedElementsByFile("/SuppressedElementsByFileAnnotation.kt"),
	SuppressedElementsByClass("/SuppressedElementsByClassAnnotation.kt");

	fun path(): Path = Paths.get(resource(file))
}
