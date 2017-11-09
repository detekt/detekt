package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
enum class Case(val file: String) {
	CasesFolder("/cases"),
	CollapsibleIfs("/cases/CollapsibleIfs.kt"),
	ConditionalPath("/cases/ConditionalPath.kt"),
	DataClassContainsFunctions("/cases/DataClassContainsFunctions.kt"),
	Default("/cases/Default.kt"),
	Empty("/cases/Empty.kt"),
	EmptyKtFile("/cases/EmptyKtFile.kt"),
	EmptyIfPositive("/cases/EmptyIfPositive.kt"),
	EmptyIfNegative("/cases/EmptyIfNegative.kt"),
	EmptyDefaultConstructor("/cases/EmptyDefaultConstructor.kt"),
	Exceptions("/cases/Exceptions.kt"),
	ExceptionRaisedInMethodsNegative("/cases/ExceptionRaisedInMethodsNegative.kt"),
	ExceptionRaisedInMethodsPositive("/cases/ExceptionRaisedInMethodsPositive.kt"),
	FinalClass("/cases/FinalClass.kt"),
	FunctionReturningConstant("/cases/FunctionReturningConstant.kt"),
	IteratorImplNegative("/cases/IteratorImplNegative.kt"),
	IteratorImplPositive("/cases/IteratorImplPositive.kt"),
	LabeledExpression("/cases/LabeledExpression.kt"),
	LoopWithTooManyJumpStatements("cases/LoopWithTooManyJumpStatements.kt"),
	NamingConventions("/cases/NamingConventions.kt"),
	NewLineAtEndOfFile("/cases/NewLineAtEndOfFile.kt"),
	MaxLineLength("/cases/MaxLineLength.kt"),
	MethodNameEqualsClassName("/cases/MethodNameEqualsClassName.kt"),
	ModifierOrder("/cases/ModifierOrder.kt"),
	OverloadedMethods("/cases/OverloadedMethods.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	ComplexInterfaceNegative("/cases/ComplexInterfaceNegative.kt"),
	ComplexInterfacePositive("/cases/ComplexInterfacePositive.kt"),
	Comments("/cases/Comments.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NestedLongMethods("/cases/NestedLongMethods.kt"),
	FeatureEnvy("/cases/FeatureEnvy.kt"),
	UnreachableCode("/cases/UnreachableCode.kt"),
	UnnecessaryAbstractClass("/cases/UnnecessaryAbstractClass.kt"),
	UtilityClasses("/cases/UtilityClasses.kt"),
	Serializable("/cases/Serializable.kt"),
	SuppressedElements("/SuppressedByElementAnnotation.kt"),
	SuppressedElementsByFile("/SuppressedElementsByFileAnnotation.kt"),
	SuppressedElementsByClass("/SuppressedElementsByClassAnnotation.kt"),
	SuppressStringLiteralDuplication("/SuppressStringLiteralDuplication.kt"),
	SwallowedException("/cases/SwallowedException.kt"),
	TooManyFunctions("/cases/TooManyFunctions.kt"),
	TooManyFunctionsTopLevel("/cases/TooManyFunctionsTopLevel.kt"),
	UseDataClass("/cases/UseDataClass.kt"),
	UnconditionalJumpStatementInLoopNegative("/cases/UnconditionalJumpStatementInLoopNegative.kt"),
	UnconditionalJumpStatementInLoopPositive("/cases/UnconditionalJumpStatementInLoopPositive.kt"),
	NestedClassesVisibility("/cases/NestedClassesVisibility.kt");

	fun path(): Path = Paths.get(resource(file))
}
