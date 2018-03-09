package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.style.KtFileContent
import io.gitlab.arturbosch.detekt.test.compileForTest
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
	ComplexMethods("/cases/ComplexMethods.kt"),
	ConditionalPath("/cases/ConditionalPath.kt"),
	DataClassContainsFunctionsPositive("/cases/DataClassContainsFunctionsPositive.kt"),
	DataClassContainsFunctionsNegative("/cases/DataClassContainsFunctionsNegative.kt"),
	Default("/cases/Default.kt"),
	Empty("/cases/Empty.kt"),
	EmptyKtFile("/cases/EmptyKtFile.kt"),
	EmptyIfPositive("/cases/EmptyIfPositive.kt"),
	EmptyIfNegative("/cases/EmptyIfNegative.kt"),
	EmptyDefaultConstructorNegative("/cases/EmptyDefaultConstructorNegative.kt"),
	EmptyDefaultConstructorPositive("/cases/EmptyDefaultConstructorPositive.kt"),
	EqualsAlwaysReturnsTrueOrFalsePositive("/cases/EqualsAlwaysReturnsTrueOrFalsePositive.kt"),
	EqualsAlwaysReturnsTrueOrFalseNegative("/cases/EqualsAlwaysReturnsTrueOrFalseNegative.kt"),
	TooGenericExceptions("/cases/TooGenericExceptions.kt"),
	ExceptionRaisedInMethodsNegative("/cases/ExceptionRaisedInMethodsNegative.kt"),
	ExceptionRaisedInMethodsPositive("/cases/ExceptionRaisedInMethodsPositive.kt"),
	FinalClassNegative("/cases/ProtectedMemberInFinalClassNegative.kt"),
	FinalClassPositive("/cases/ProtectedMemberInFinalClassPositive.kt"),
	FunctionReturningConstantPositive("/cases/FunctionReturningConstantPositive.kt"),
	FunctionReturningConstantNegative("/cases/FunctionReturningConstantNegative.kt"),
	IteratorImplNegative("/cases/IteratorImplNegative.kt"),
	IteratorImplPositive("/cases/IteratorImplPositive.kt"),
	LabeledExpression("/cases/LabeledExpression.kt"),
	LoopWithTooManyJumpStatementsNegative("cases/LoopWithTooManyJumpStatementsNegative.kt"),
	LoopWithTooManyJumpStatementsPositive("cases/LoopWithTooManyJumpStatementsPositive.kt"),
	NamingConventions("/cases/NamingConventions.kt"),
	NewLineAtEndOfFile("/cases/NewLineAtEndOfFile.kt"),
	MaxLineLength("/cases/MaxLineLength.kt"),
	MemberNameEqualsClassNameNegative("/cases/MemberNameEqualsClassNameNegative.kt"),
	MemberNameEqualsClassNamePositive("/cases/MemberNameEqualsClassNamePositive.kt"),
	OverloadedMethods("/cases/OverloadedMethods.kt"),
	ComplexClass("/cases/ComplexClass.kt"),
	ComplexInterfaceNegative("/cases/ComplexInterfaceNegative.kt"),
	ComplexInterfacePositive("/cases/ComplexInterfacePositive.kt"),
	Comments("/cases/Comments.kt"),
	NestedClasses("/cases/NestedClasses.kt"),
	NoClasses("/cases/NoClasses.kt"),
	LongMethodPositive("/cases/LongMethodPositive.kt"),
	LongMethodNegative("/cases/LongMethodNegative.kt"),
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
	NestedClassVisibilityPositive("/cases/NestedClassVisibilityPositive.kt"),
	NestedClassVisibilityNegative("/cases/NestedClassVisibilityNegative.kt"),
	TrailingWhitespaceNegative("/cases/TrailingWhitespaceNegative.kt"),
	TrailingWhitespacePositive("/cases/TrailingWhitespacePositive.kt"),
	NoTabsNegative("/cases/NoTabsNegative.kt"),
	NoTabsPositive("/cases/NoTabsPositive.kt");

	fun path(): Path = Paths.get(resource(file))

	fun getKtFileContent(): KtFileContent {
		val file = compileForTest(path())
		val lines = file.text.splitToSequence("\n")
		val ktFileContent = KtFileContent(file, lines)
		return ktFileContent
	}
}
