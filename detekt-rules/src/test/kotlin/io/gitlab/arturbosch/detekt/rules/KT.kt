package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
enum class Case(val file: String) {
	CasesFolder("/cases"),
	CollapsibleIfsPositive("/cases/CollapsibleIfsPositive.kt"),
	CollapsibleIfsNegative("/cases/CollapsibleIfsNegative.kt"),
	ConditionalPath("/cases/ConditionalPath.kt"),
	DataClassContainsFunctionsPositive("/cases/DataClassContainsFunctionsPositive.kt"),
	DataClassContainsFunctionsNegative("/cases/DataClassContainsFunctionsNegative.kt"),
	Default("/cases/Default.kt"),
	Empty("/cases/Empty.kt"),
	EmptyKtFile("/cases/EmptyKtFile.kt"),
	EmptyIfPositive("/cases/EmptyIfPositive.kt"),
	EmptyIfNegative("/cases/EmptyIfNegative.kt"),
	EmptyDefaultConstructor("/cases/EmptyDefaultConstructor.kt"),
	Exceptions("/cases/Exceptions.kt"),
	ExceptionRaisedInMethodsNegative("/cases/ExceptionRaisedInMethodsNegative.kt"),
	ExceptionRaisedInMethodsPositive("/cases/ExceptionRaisedInMethodsPositive.kt"),
	FinalClassNegative("/cases/ProtectedMemberInFinalClassNegative.kt"),
	FinalClassPositive("/cases/ProtectedMemberInFinalClassPositive.kt"),
	FunctionReturningConstant("/cases/FunctionReturningConstant.kt"),
	IteratorImplNegative("/cases/IteratorImplNegative.kt"),
	IteratorImplPositive("/cases/IteratorImplPositive.kt"),
	LabeledExpression("/cases/LabeledExpression.kt"),
	LoopWithTooManyJumpStatementsNegative("cases/LoopWithTooManyJumpStatementsNegative.kt"),
	LoopWithTooManyJumpStatementsPositive("cases/LoopWithTooManyJumpStatementsPositive.kt"),
	NamingConventions("/cases/NamingConventions.kt"),
	NewLineAtEndOfFile("/cases/NewLineAtEndOfFile.kt"),
	MaxLineLength("/cases/MaxLineLength.kt"),
	MethodNameEqualsClassNameNegative("/cases/MethodNameEqualsClassNameNegative.kt"),
	MethodNameEqualsClassNamePositive("/cases/MethodNameEqualsClassNamePositive.kt"),
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
	UnnecessaryAbstractClassPositive("/cases/UnnecessaryAbstractClassPositive.kt"),
	UnnecessaryAbstractClassNegative("/cases/UnnecessaryAbstractClassNegative.kt"),
	UtilityClassesPositive("/cases/UtilityClassesPositive.kt"),
	UtilityClassesNegative("/cases/UtilityClassesNegative.kt"),
	SerializablePositive("/cases/SerializablePositive.kt"),
	SerializableNegative("/cases/SerializableNegative.kt"),
	SuppressedElements("/SuppressedByElementAnnotation.kt"),
	SuppressedElementsByFile("/SuppressedElementsByFileAnnotation.kt"),
	SuppressedElementsByClass("/SuppressedElementsByClassAnnotation.kt"),
	SuppressStringLiteralDuplication("/SuppressStringLiteralDuplication.kt"),
	SwallowedExceptionNegative("/cases/SwallowedExceptionNegative.kt"),
	SwallowedExceptionPositive("/cases/SwallowedExceptionPositive.kt"),
	TooManyFunctions("/cases/TooManyFunctions.kt"),
	TooManyFunctionsTopLevel("/cases/TooManyFunctionsTopLevel.kt"),
	UseDataClassNegative("/cases/UseDataClassNegative.kt"),
	UseDataClassPositive("/cases/UseDataClassPositive.kt"),
	UnconditionalJumpStatementInLoopNegative("/cases/UnconditionalJumpStatementInLoopNegative.kt"),
	UnconditionalJumpStatementInLoopPositive("/cases/UnconditionalJumpStatementInLoopPositive.kt"),
	NestedClassesVisibility("/cases/NestedClassesVisibility.kt");

	fun path(): Path = Paths.get(resource(file))
}
